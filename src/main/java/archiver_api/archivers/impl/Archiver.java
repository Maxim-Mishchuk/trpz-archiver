package archiver_api.archivers.impl;

import archiver_api.output.Entity;
import archiver_api.supported_types.ArchiveType;
import archiver_api.actions.delete.ArchiveDeleter;
import archiver_api.actions.read.ArchiveReader;
import archiver_api.actions.write.ArchiveWriter;
import archiver_api.archivers.AbstractArchiver;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Archiver extends AbstractArchiver {
    protected final ArchiveType archiveType;
    protected final ArchiveWriter writer;
    protected final ArchiveReader reader;
    protected final ArchiveDeleter deleter;

    public Archiver(ArchiveType archiveType, ArchiveWriter writer, ArchiveReader reader, ArchiveDeleter deleter) {
        this.writer = writer;
        this.reader = reader;
        this.deleter = deleter;
        this.archiveType = archiveType;
    }

    @Override
    public void create(Path archivePath, List<Path> filePaths) throws IOException, ArchiveException {
        try (
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(archivePath));
                ArchiveOutputStream aos = new ArchiveStreamFactory()
                        .createArchiveOutputStream(archiveType.name(), bos)
        ) {
            writer.write(aos, filePaths);
        }
    }

    @Override
    public void add(Path archivePath, List<Path> filePaths) throws IOException, ArchiveException {
        Path tempArchivePath = createTempArchive("archives", archivePath);
        try (
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(archivePath));
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(tempArchivePath));
                ArchiveInputStream ais = new ArchiveStreamFactory()
                        .createArchiveInputStream(archiveType.name(), bis);
                ArchiveOutputStream aos = new ArchiveStreamFactory()
                        .createArchiveOutputStream(archiveType.name(), bos)

        ) {
            writer.write(ais, aos, filePaths);
        } finally {
            Files.copy(tempArchivePath, archivePath, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(tempArchivePath);
        }
    }

    @Override
    public List<Entity> read(Path archivePath) throws IOException, ArchiveException {
        try (
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(archivePath));
                ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(bis)
        ) {
            return reader.read(ais);
        }
    }

    @Override
    public List<Entity> delete(Path archivePath, List<String> fileNames) throws IOException, ArchiveException {
        Path tempArchivePath = createTempArchive("archives", archivePath);

        List<Entity> deletedEntities = null;
        try (
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(archivePath));
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(tempArchivePath));
                ArchiveInputStream ais = new ArchiveStreamFactory()
                        .createArchiveInputStream(archiveType.name(), bis);
                ArchiveOutputStream aos = new ArchiveStreamFactory()
                        .createArchiveOutputStream(archiveType.name(), bos)
        ) {
            deletedEntities = deleter.delete(ais, aos, fileNames);
            return deletedEntities;
        } finally {
            if (deletedEntities != null) {
                Files.copy(tempArchivePath, archivePath, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.delete(tempArchivePath);
        }
    }
}
