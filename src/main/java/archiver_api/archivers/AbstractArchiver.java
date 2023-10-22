package archiver_api.archivers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class AbstractArchiver implements IArchiver {
    private final String TEMP_FOLDER = "temp";
    protected Path createTempArchive(String folder, Path archivePath) throws IOException {
        Path tempArchivePath = Path.of(TEMP_FOLDER, folder, archivePath.getFileName().toString());
        return Files.copy(archivePath, tempArchivePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
