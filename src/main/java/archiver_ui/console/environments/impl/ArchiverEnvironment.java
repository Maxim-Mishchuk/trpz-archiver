package archiver_ui.console.environments.impl;

import archiver_api.archivers.AbstractArchiver;
import archiver_api.archivers.impl.BasicArchiverCreator;
import archiver_api.output.Entity;
import archiver_api.output.ExportVisitor;
import archiver_api.archivers.actionTypes.ArchiveReadingType;
import archiver_api.supported_types.ArchiveType;
import archiver_api.supported_types.CompressorType;
import archiver_ui.console.environments.Environment;
import archiver_ui.console.input.FormBuilder;
import archiver_ui.console.input.InputPredicates;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.file.Path;
import java.util.*;

public class ArchiverEnvironment extends Environment {

    public ArchiverEnvironment(InputStream is, PrintStream os) {
        super(is, os);
        this.inputType = "archiver> ";
    }

    protected void analyze(String command) {
        switch (command) {
            case "new" -> create();
            case "add" -> add();
            case "remove" -> remove();
            case "show" -> show();
            case "extract" -> extract();
            default -> out.println("incorrect command: " + command);
        }
    }

    private void create() {
        Map<String, String> res = new FormBuilder(in, out)
                .addField("Enter filename: ", "name", InputPredicates.isFileName, "Incorrect filename!")
                .addField("Enter archive type: ", "archType", InputPredicates.isArchiveType, "Incorrect archive type!")
                .addField("Enter compressor type (press enter if it's unnecessary): ", "compressType", InputPredicates.isCompressorType, "Incorrect compress type!")
                .addField("Enter save path: ", "path", InputPredicates.isExistedPath, "Path is incorrect or file/directory does not exist!")
                .addField("Enter file path to add (press enter to end): ", "filePaths", InputPredicates.isExistedPath, "Path is incorrect or file/directory does not exist!", true, "|")
                .build()
                .execute();

        String archiveName;
        ArchiveType archType = getArchiveType(res.get("archType"));
        CompressorType compressType;
        if (res.get("compressType").isBlank()) {
            archiveName = res.get("name") + "." + archType.name().toLowerCase();
            compressType = null;
        } else {
            archiveName = res.get("name") + "." + archType.name().toLowerCase() + "." + res.get("compressType").toLowerCase();
            compressType = getCompressorType(res.get("compressType"));
        }

        Path archivePath = Path.of(res.get("path"), archiveName);
        List<Path> filePaths = convertMultipleStringToPathList(res.get("filePaths"), "\\|");

        try {
            if (compressType != null) {
                getBasicArchiver(archType, compressType)
                        .create(archivePath, filePaths);
            } else {
                getBasicArchiver(archType)
                        .create(archivePath, filePaths);
            }
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }

        out.println("\nArchive " + archiveName + " was successfully created by the path: " + archivePath + "\n");
    }

    private void add() {
        Map<String, String> res = new FormBuilder(in, out)
                .addField("Enter archive path: ", "path", InputPredicates.isCorrectArchivePath, "Path is incorrect or archive does not be supported!")
                .addField("Enter file path to add (press enter to end): ", "filePaths", InputPredicates.isExistedPath, "Path is incorrect or file/directory does not exist!", true, "|")
                .build()
                .execute();

        Path archivePath = Path.of(res.get("path"));
        Pair<ArchiveType, CompressorType> pairTypes = getArchiveAndCompressorTypesFromPath(archivePath);
        List<Path> filePaths = convertMultipleStringToPathList(res.get("filePaths"), "\\|");

        try {
            if (pairTypes.getRight() != null) {
                getBasicArchiver(pairTypes.getLeft(), pairTypes.getRight())
                        .add(archivePath, filePaths);
            } else {
                getBasicArchiver(pairTypes.getLeft())
                        .create(archivePath, filePaths);
            }
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }

        out.println("\nArchive " + archivePath.getFileName() + " was successfully added the files\n");
    }

    private void show() {
        Map<String, String> res = new FormBuilder(in, out)
                .addField("Enter archive path: ", "path", InputPredicates.isCorrectArchivePath, "Path is incorrect or archive does not be supported!")
                .build()
                .execute();

        Path archivePath = Path.of(res.get("path"));
        Pair<ArchiveType, CompressorType> pairTypes = getArchiveAndCompressorTypesFromPath(archivePath);

        List<Entity> entities;
        try {
            if (pairTypes.getRight() != null) {
                entities = getBasicArchiver(pairTypes.getLeft(), pairTypes.getRight())
                        .read(archivePath, ArchiveReadingType.BASIC);
            } else {
                entities = getBasicArchiver(pairTypes.getLeft())
                        .read(archivePath, ArchiveReadingType.BASIC);
            }
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }

        out.println("\n----- Archive's files (" + archivePath + ") -----");
        entities.stream()
                .map(Entity::name)
                .forEach(out::println);
        out.println();
    }

    private void remove() {
        Map<String, String> res = new FormBuilder(in, out)
                .addField("Enter archive path: ", "path", InputPredicates.isCorrectArchivePath, "Path is incorrect or archive does not be supported!")
                .addField("Enter entry name (press enter to end): ", "entryNames", InputPredicates.isEntryName, "Entry name is incorrect", true, "|")
                .build()
                .execute();

        Path archivePath = Path.of(res.get("path"));
        Pair<ArchiveType, CompressorType> pairTypes = getArchiveAndCompressorTypesFromPath(archivePath);
        List<String> entryNames = convertMultipleStringToStringList(res.get("entryNames"), "\\|");

        List<Entity> entities;
        try {
            if (pairTypes.getRight() != null) {
                entities = getBasicArchiver(pairTypes.getLeft(), pairTypes.getRight())
                        .delete(archivePath, entryNames);
            } else {
                entities = getBasicArchiver(pairTypes.getLeft())
                        .delete(archivePath, entryNames);
            }
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }

        out.println("\n----- Successfully deleted -----");
        entities.stream()
                .map(Entity::name)
                .forEach(out::println);
        out.println();
    }

    private void extract() {
        Map<String, String> res = new FormBuilder(in, out)
                .addField("Enter archive path: ", "archivePath", InputPredicates.isCorrectArchivePath, "Path is incorrect or file/directory does not exist!")
                .addField("Enter extract path: ", "extractPath", InputPredicates.isExistedDirectory, "Path is incorrect or directory does not exist!")
                .build()
                .execute();

        Path archivePath = Path.of(res.get("archivePath"));
        Pair<ArchiveType, CompressorType> pairTypes = getArchiveAndCompressorTypesFromPath(archivePath);

        List<Entity> entities;
        try {
            if (pairTypes.getRight() != null) {
                entities = getBasicArchiver(pairTypes.getLeft(), pairTypes.getRight())
                        .read(archivePath, ArchiveReadingType.FULL);
            } else {
                entities = getBasicArchiver(pairTypes.getLeft())
                        .read(archivePath, ArchiveReadingType.FULL);
            }
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }

        Path extractPath = Path.of(res.get("extractPath"));

        ExportVisitor visitor = new ExportVisitor(extractPath);
        visitor.export(entities);

        out.println("\nArchive " + archivePath.getFileName() + " was successfully extracted to " + extractPath + "\n");
    }

    private AbstractArchiver getBasicArchiver(ArchiveType archType) {
        return new BasicArchiverCreator().createArchiver(archType);
    }

    private AbstractArchiver getBasicArchiver(ArchiveType archType, CompressorType compressType) {
        return new BasicArchiverCreator().createArchiver(archType, compressType);
    }

    private List<Path> convertMultipleStringToPathList(String paths, String splitter) {
        return Arrays.stream(paths.split(splitter))
                .map(Path::of)
                .toList();
    }

    private List<String> convertMultipleStringToStringList(String paths, String splitter) {
        return Arrays.stream(paths.split(splitter))
                .toList();
    }

    private ArchiveType getArchiveType(String type) {
        return ArchiveType.valueOf(type.toUpperCase());
    }
    private CompressorType getCompressorType(String type) {
        return CompressorType.valueOf(type.toUpperCase());
    }

    private Pair<ArchiveType, CompressorType> getArchiveAndCompressorTypesFromPath(Path archivePath) {
        String fileExtension = FileNameUtils.getExtension(archivePath);
        ArchiveType archType;
        CompressorType compressType;
        if (InputPredicates.isArchiveType.test(fileExtension)) {
            archType = getArchiveType(fileExtension);
            compressType = null;
        } else {
            archType = getArchiveType(
                    FileNameUtils.getExtension(
                            FileNameUtils.getBaseName(archivePath)
                    )
            );
            compressType = getCompressorType(fileExtension);
        }
        return new ImmutablePair<>(archType, compressType);
    }
}
