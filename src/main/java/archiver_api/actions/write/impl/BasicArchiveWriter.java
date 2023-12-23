package archiver_api.actions.write.impl;

import archiver_api.actions.write.ArchiveWriter;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class BasicArchiveWriter implements ArchiveWriter {
    @Override
    public void write(ArchiveOutputStream aos, List<Path> pathList) throws IOException {
        for (Path path : pathList) {
            if (Files.isDirectory(path)) {
                List<Path> localPathList = getAllFilePathsFromDirectory(path);
                for (Path localPath: localPathList) {
                    String cutName = cutPathName(path, localPath);
                    ArchiveEntry newEntry = aos.createArchiveEntry(localPath, cutName);

                    aos.putArchiveEntry(newEntry);
                    try (InputStream is = Files.newInputStream(localPath)) {
                        IOUtils.copy(is, aos);
                    }
                    aos.closeArchiveEntry();
                }
            } else {
                ArchiveEntry newEntry = aos.createArchiveEntry(path, path.getFileName().toString());
                aos.putArchiveEntry(newEntry);
                try (InputStream is = Files.newInputStream(path)) {
                    IOUtils.copy(is, aos);
                }
                aos.closeArchiveEntry();
            }
        }
    }

    @Override
    public void write(ArchiveInputStream ais, ArchiveOutputStream aos, List<Path> pathList) throws IOException {
        ArchiveEntry currentEntry;
        List<String> namesList = new LinkedList<>();
        for (Path path : pathList) {
            if (Files.isDirectory(path)) {
                List<Path> localPaths = getAllFilePathsFromDirectory(path);
                namesList.addAll(
                        localPaths.stream().map(localPath -> cutPathName(path, localPath)).toList()
                );
            } else {
                namesList.add(path.getFileName().toString());
            }
        }
        while ((currentEntry = ais.getNextEntry()) != null) {
            String entryName = Path.of(currentEntry.getName()).toString();

            if (!namesList.contains(entryName)) {
                aos.putArchiveEntry(currentEntry);
                IOUtils.copy(ais, aos);
                aos.closeArchiveEntry();
            }
        }
        write(aos, pathList);
    }

    private String cutPathName(Path rootPath, Path filePath) {
        return filePath.toString()
                .replace(rootPath.getParent().toString(), "");
    }

    private List<Path> getAllFilePathsFromDirectory(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(Files::isRegularFile).toList();
        }
    }
}
