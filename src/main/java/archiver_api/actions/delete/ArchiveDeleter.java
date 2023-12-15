package archiver_api.actions.delete;

import java.io.IOException;
import java.util.List;

import archiver_api.output.Entity;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;

public interface ArchiveDeleter {
    List<Entity> delete(ArchiveInputStream ais, ArchiveOutputStream aos, List<String> entityNames) throws IOException;
}
