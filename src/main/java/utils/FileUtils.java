package utils;

import org.apache.commons.compress.utils.FileNameUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static Path getFreePath(Path path) {
        Path parentPath = path.getParent();

        if (Files.exists(path)) {
            String name = FileNameUtils.getBaseName(path);
            StringBuilder extension = new StringBuilder();
            extension.insert(0, FileNameUtils.getExtension(path));

            while (!FileNameUtils.getExtension(name).isBlank()) {
                extension.insert(0, FileNameUtils.getExtension(name) + ".");
                name = FileNameUtils.getBaseName(name);
            }


            for (int i = 1; Files.exists(path); i++) {
                path = parentPath.resolve(name + "(" + i + ")" + "." + extension);
            }
        }

        return path;
    }
}
