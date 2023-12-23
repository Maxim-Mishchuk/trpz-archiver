package archiver_ui.console.environments.impl;

import archiver_ui.console.environments.Environment;
import archiver_ui.console.input.FormBuilder;
import archiver_ui.console.input.InputPredicates;
import fileTransfer.P2PFileTransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;

public class FileTransferEnvironment extends Environment {
    private final P2PFileTransfer p2pFileTransfer = new P2PFileTransfer();

    public FileTransferEnvironment(InputStream in, PrintStream out) {
        super(in, out);
        this.inputType = "fileTransfer> ";
    }

    @Override
    protected void analyze(String command) {
        switch (command) {
            case "send" -> send();
            case "init" -> init();
        }
    }

    private void send() {
        Map<String, String> res = new FormBuilder(in, out)
                .addField("Enter ip-address:", "ip", InputPredicates.isCorrectIp, "Incorrect from of ip-address!")
                .addField("Enter file path:", "filePath", InputPredicates.isExistedFile, "Path is incorrect or file does not exist!")
                .build()
                .execute();

        Path filePath = Path.of(res.get("filePath"));
        try {
            p2pFileTransfer.send(res.get("ip"), filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        Thread server = p2pFileTransfer.start();

        out.println("\n----- Press any button to stop the server -----\n");
        try {
            in.read();

            server.interrupt();
            out.println("\n----- Process of stopping is started -----\n");

            server.join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
