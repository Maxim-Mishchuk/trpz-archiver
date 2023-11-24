package archiver_api.actions.write;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ArchiveWriter {
    void write(ArchiveOutputStream aos, List<Path> pathList) throws IOException;
    void write(ArchiveInputStream ais, ArchiveOutputStream aos, List<Path> pathList) throws IOException;
}
