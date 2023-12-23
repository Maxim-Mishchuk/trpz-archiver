package archiver_api.archivers;

import org.apache.commons.compress.utils.FileNameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        String name = FileNameUtils.getBaseName(archivePath);
        String extension = FileNameUtils.getExtension(archivePath);
        Path tempArchivePath = Path.of(TEMP_FOLDER, folder, name + "." + extension);

        for (int i = 1; Files.exists(tempArchivePath); i++) {
            tempArchivePath = Path.of(TEMP_FOLDER, folder, name + i + "." + extension);
        }

        return Files.createFile(tempArchivePath);
    }
}
