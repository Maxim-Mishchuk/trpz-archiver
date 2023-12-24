package archiver_api.output;

import utils.FileUtils;

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
            entityPath = FileUtils.getFreePath(entityPath);

            try (OutputStream os = Files.newOutputStream(entityPath)) {
                os.write(entity.bytes());
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
