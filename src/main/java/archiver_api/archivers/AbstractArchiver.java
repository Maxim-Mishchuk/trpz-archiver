package archiver_api.archivers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class AbstractArchiver implements IArchiver {
    private final String TEMP_FOLDER = "temp";

    private void initTempFolder() throws IOException{
        Path tempPath = Path.of(TEMP_FOLDER);
        if (Files.notExists(tempPath))
            Files.createDirectory(tempPath);
    }

    protected Path createTempArchive(String folder, Path archivePath) throws IOException {
        initTempFolder();
        Path folderPath = Path.of(TEMP_FOLDER, folder);
        if (Files.notExists(folderPath))
            Files.createDirectory(folderPath);

        Path tempArchivePath = Path.of(TEMP_FOLDER, folder, archivePath.getFileName().toString());
        return Files.createFile(tempArchivePath);
    }
}
