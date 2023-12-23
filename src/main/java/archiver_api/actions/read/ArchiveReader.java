package archiver_api.actions.read;

import archiver_api.output.Entity;
import org.apache.commons.compress.archivers.ArchiveInputStream;

import java.io.IOException;
import java.util.List;

public interface ArchiveReader {
    List<Entity> readBasic(ArchiveInputStream ais) throws IOException;
    List<Entity> readFull(ArchiveInputStream ais) throws IOException;
}
