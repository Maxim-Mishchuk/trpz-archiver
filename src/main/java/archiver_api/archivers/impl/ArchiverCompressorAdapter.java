package archiver_api.archivers.impl;

import archiver_api.output.Entity;
import archiver_api.archivers.AbstractArchiver;
import archiver_api.compressors.impl.Compressor;
import archiver_api.archivers.actionTypes.ArchiveReadingType;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.compressors.CompressorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

public class ArchiverCompressorAdapter extends AbstractArchiver {
    private final AbstractArchiver archiver;
    private final Compressor compressor;

    public ArchiverCompressorAdapter(AbstractArchiver archiver, Compressor compressor) {
        this.archiver = archiver;
        this.compressor = compressor;
    }

    @Override
    public void create(Path archivePath, List<Path> filePaths) throws IOException, ArchiveException {
        Path tempPath = createTempArchive("compress", archivePath);
        archiver.create(tempPath, filePaths);
        try {
            compressor.compress(tempPath, archivePath);
        } catch (CompressorException e) {
            throw new RuntimeException(e);
        } finally {
            Files.delete(tempPath);
        }
    }

    @Override
    public void add(Path archivePath, List<Path> filePaths) throws IOException, ArchiveException {
        Path tempPath = createTempArchive("compress", archivePath);

        try {
            compressor.decompress(archivePath, tempPath);
            archiver.add(tempPath, filePaths);
            compressor.compress(tempPath, archivePath);
        } catch (CompressorException e) {
            throw new RuntimeException(e);
        } finally {
            Files.delete(tempPath);
        }
    }

    @Override
    public List<Entity> read(Path archivePath, ArchiveReadingType archiveReadingType) throws IOException, ArchiveException {
        Path tempPath = createTempArchive("compress", archivePath);

        try {
            compressor.decompress(archivePath, tempPath);
            return archiver.read(tempPath, archiveReadingType);
        } catch (CompressorException e) {
            throw new RuntimeException(e);
        } finally {
            Files.delete(tempPath);
        }
    }

    @Override
    public List<Entity> delete(Path archivePath, List<String> fileNames) throws IOException, ArchiveException {
        Path tempPath = createTempArchive("compress", archivePath);

        List<Entity> deletedEntities;
        try {
            compressor.decompress(archivePath, tempPath);
            deletedEntities = archiver.delete(tempPath, fileNames);
            compressor.compress(tempPath, archivePath);

            return deletedEntities;
        } catch (CompressorException e) {
            throw new RuntimeException(e);
        } finally {
            Files.delete(tempPath);
        }
    }
}
