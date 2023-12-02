package archiver_api.output;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportVisitor implements Visitor {
    private final Path exportPath;

    public ExportVisitor(Path exportPath) {
        this.exportPath = exportPath;
    }

    public void export(List<Entity> entityList) {
        for (Entity entity : entityList) {
            entity.accept(this);
        }
    }

    @Override
    public void visit(Entity entity) {
        Path entityPath = exportPath.resolve(entity.name());
        try {
            Files.createDirectories(entityPath.getParent());
            try (OutputStream os = Files.newOutputStream(entityPath)) {
                os.write(entity.bytes());
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
