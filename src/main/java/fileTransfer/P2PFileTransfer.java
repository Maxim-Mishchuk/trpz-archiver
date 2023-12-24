package fileTransfer;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.FileUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

public class P2PFileTransfer {
    private static final Logger logger = LogManager.getLogger();
    private static final int DEFAULT_PORT = 44305;
    private final FileReceiver fileReceiver = new FileReceiver("received");
    private final FileSender fileSender = new FileSender();

    private static class FileSender {
        public void send(String address, Path path) throws IOException {
            try (
                    InputStream is = Files.newInputStream(path);
                    Socket connection = new Socket(address, DEFAULT_PORT);
                    DataOutputStream dos = new DataOutputStream(connection.getOutputStream())
            ) {
                String fileName = path.getFileName().toString();

                BasicFileAttributes bfa = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                long creationTime = bfa.creationTime().toMillis();
                long lastModifiedTime = bfa.lastModifiedTime().toMillis();

                dos.writeUTF(fileName);
                dos.writeLong(creationTime);
                dos.writeLong(lastModifiedTime);
                dos.writeLong(Files.size(path));

                IOUtils.copy(is, dos, 4096);
            }
        }
    }

    private record FileReceiver(String receiveDirName) {

        public void receive(Socket clientSocket) throws IOException {
                try (
                        DataInputStream dis = new DataInputStream(clientSocket.getInputStream())
                ) {
                    Path receiveDir = initReceiveDir();
                    receiveDir = initTodayDir(receiveDir);

                    String fileName = dis.readUTF();
                    Path filePath = initReceivedFile(dis, receiveDir, fileName);

                    long fileSize = dis.readLong();
                    byte[] buffer = new byte[4096];
                    try (OutputStream os = Files.newOutputStream(filePath, StandardOpenOption.WRITE)) {
                        int bytes;
                        while (fileSize > 0 && (bytes = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                            os.write(buffer, 0, bytes);
                            fileSize -= bytes;
                        }
                    }
                    logger.info("Client server successfully saved file " + fileName + ", path: " + filePath.toAbsolutePath());
                }
        }

        private Path initReceiveDir() throws IOException {
            return Files.createDirectories(Path.of(receiveDirName));
        }

        private Path initTodayDir(Path receiveDir) throws IOException {
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            String dirName = s.format(new Date());
            Path todayDir = receiveDir.resolve(dirName);
            return Files.createDirectories(todayDir);
        }

        private Path initReceivedFile(DataInputStream dis, Path receiveDir, String fileName) throws IOException {
            Path filePath = receiveDir.resolve(fileName);
            filePath = FileUtils.getFreePath(filePath);

            FileTime creationTime = FileTime.fromMillis(dis.readLong());
            FileTime lastModifiedTime = FileTime.fromMillis(dis.readLong());

            Path result = Files.createFile(filePath);
            Files.setAttribute(filePath, "basic:creationTime", creationTime, LinkOption.NOFOLLOW_LINKS);
            Files.setAttribute(filePath, "basic:lastModifiedTime", lastModifiedTime, LinkOption.NOFOLLOW_LINKS);

            return result;
        }

        }

    public Thread start() {
        Thread serverThread = new Thread(() -> {
            logger.info("Client server has been started");
            while (!Thread.interrupted()) {
                try {
                    init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            logger.info("Client server has been stopped");
        });
        serverThread.start();
        return serverThread;
    }

    public void init() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            logger.info("Client server is waiting for connection");
            serverSocket.setSoTimeout(15_000);
            try (Socket client = serverSocket.accept()) {
                logger.info("Client server has got a connection");
                fileReceiver.receive(client);
            } catch (SocketTimeoutException ex) {
                logger.info("Client server does not get any connection");
            }
        }
    }

    public void send(String address, Path path) throws IOException {
        logger.info("Client send a file " + path.getFileName().toString());
        fileSender.send(address, path);
    }
}
