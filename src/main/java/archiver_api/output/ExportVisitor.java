package archiver_api.output;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportVisitor implements Visitor {
    private final Path exportPath;

    public ExportVisitor(Path exportPath) {
        if (!Files.isDirectory(exportPath)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }
        this.exportPath = exportPath;
    }

    public void export(List<Entity> entityList) {
        entityList.forEach(entity -> entity.accept(this));
    }

    @Override
    public void visit(Entity entity) {
        Path entityPath = exportPath.resolve(entity.name());
        Path parentPath = entityPath.getParent();
        try {
            Files.createDirectories(parentPath);

            if (Files.exists(entityPath)) {
                String name = FileNameUtils.getBaseName(entityPath);
                String extension = FileNameUtils.getExtension(entityPath);

                for (int i = 1; Files.exists(entityPath); i++) {
                    entityPath = parentPath.resolve(name + "(" + i + ")" + "." + extension);
                }
            }

            try (OutputStream os = Files.newOutputStream(entityPath)) {
                os.write(entity.bytes());
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
