package archiver_api.archivers;

import archiver_api.supported_types.CompressorType;
import archiver_api.supported_types.ArchiveType;

public interface ArchiverCreator {
    AbstractArchiver createArchiver(ArchiveType archiveType);
    AbstractArchiver createArchiver(ArchiveType archiveType, CompressorType compressorType);
}
