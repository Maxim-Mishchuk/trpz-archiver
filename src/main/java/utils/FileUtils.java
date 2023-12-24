package utils;

import org.apache.commons.compress.utils.FileNameUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static Path getFreePath(Path path) {
        Path parentPath = path.getParent();

        if (Files.exists(path)) {
            String name = FileNameUtils.getBaseName(path);

            while (FileNameUtils.getExtension(name).isBlank()) {
                name = FileNameUtils.getBaseName(name);
            }

            String extension = FileNameUtils.getExtension(path);

            for (int i = 1; Files.exists(path); i++) {
                path = parentPath.resolve(name + "(" + i + ")" + "." + extension);
            }
        }

        return path;
    }
}
