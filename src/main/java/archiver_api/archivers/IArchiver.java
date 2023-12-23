package archiver_api.archivers;

import archiver_api.output.Entity;
import archiver_api.archivers.actionTypes.ArchiveReadingType;
import org.apache.commons.compress.archivers.ArchiveException;

import java.io.IOException;
import java.util.List;
import java.nio.file.Path;

public interface IArchiver {
    void create(Path archivePath, List<Path> filePaths) throws IOException, ArchiveException;
    void add(Path archivePath, List<Path> filePaths) throws IOException, ArchiveException;
    List<Entity> read(Path archivePath, ArchiveReadingType archiveReadingType) throws IOException, ArchiveException;
    List<Entity> delete(Path archivePath, List<String> fileNames) throws IOException, ArchiveException;
}
