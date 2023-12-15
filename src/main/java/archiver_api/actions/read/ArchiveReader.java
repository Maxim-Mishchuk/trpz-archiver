package archiver_api.actions.read;

import archiver_api.output.Entity;
import org.apache.commons.compress.archivers.ArchiveInputStream;

import java.io.IOException;
import java.util.List;

public interface ArchiveReader {
    List<Entity> read(ArchiveInputStream ais) throws IOException;
}
