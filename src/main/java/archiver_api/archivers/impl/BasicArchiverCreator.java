package archiver_api.archivers.impl;

import archiver_api.supported_types.CompressorType;
import archiver_api.supported_types.ArchiveType;
import archiver_api.actions.delete.impl.BasicArchiveDeleter;
import archiver_api.actions.read.impl.BasicArchiveReader;
import archiver_api.actions.write.impl.BasicArchiveWriter;
import archiver_api.archivers.AbstractArchiver;
import archiver_api.archivers.ArchiverCreator;
import archiver_api.compressors.impl.Compressor;

public class BasicArchiverCreator implements ArchiverCreator {
    @Override
    public AbstractArchiver createArchiver(ArchiveType archiveType) {
        return new Archiver(archiveType, new BasicArchiveWriter(), new BasicArchiveReader(), new BasicArchiveDeleter());
    }

    @Override
    public AbstractArchiver createArchiver(ArchiveType archiveType, CompressorType compressorType) {
        return new ArchiverCompressorAdapter(
                createArchiver(archiveType),
                new Compressor(compressorType)
        );
    }
}
