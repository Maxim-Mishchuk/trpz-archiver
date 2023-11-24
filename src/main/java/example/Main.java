package example;

import archiver_api.supported_types.CompressorType;
import archiver_api.supported_types.ArchiveType;
import archiver_api.archivers.AbstractArchiver;
import archiver_api.archivers.ArchiverCreator;
import archiver_api.archivers.impl.BasicArchiverCreator;
import org.apache.commons.compress.archivers.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
       test();
    }

    private static void test() {
        String resources = "src/main/resources";
        String inputResources = resources + "/input";
        Path archivePath = Path.of(resources, "output", "test.zip");
        List<Path> createList = List.of(
                Path.of(inputResources, "file1.txt"),
                Path.of(inputResources, "file2.txt")
        );

        List<Path> addList = List.of(
                Path.of(inputResources, "file3.txt"),
                Path.of(inputResources, "dir")
        );

        List<Path> changedList = List.of(
                Path.of(inputResources, "changed/file3.txt"),
                Path.of(inputResources, "changed/dir")
        );

        List<String> deleteList = List.of(
                "dir/file_dir.txt"
        );

        ArchiverCreator creator = new BasicArchiverCreator();
        AbstractArchiver archiver = creator.createArchiver(ArchiveType.ZIP);

        try {
            initOutputFolder(resources);

            archiver.create(archivePath, createList);
            archiver.add(archivePath, addList);
            archiver.add(archivePath, changedList);
            System.out.println(archiver.read(archivePath));
            System.out.println(archiver.delete(archivePath, deleteList));
        } catch (IOException | ArchiveException e) {
            e.printStackTrace();
        }
    }

    private static void initOutputFolder(String resources) throws IOException {
        Path outputPath = Path.of(resources, "output");
        if (Files.notExists(outputPath))
            Files.createDirectory(outputPath);
    }
}
