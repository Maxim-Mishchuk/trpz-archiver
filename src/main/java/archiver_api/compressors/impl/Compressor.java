package archiver_api.compressors.impl;

import archiver_api.supported_types.CompressorType;
import archiver_api.compressors.ICompressor;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Compressor implements ICompressor {
    private final CompressorType type;

    public Compressor(CompressorType type) {
        this.type = type;
    }

    @Override
    public void compress(Path archivePath, Path savePath) throws IOException, CompressorException {
        try (
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(archivePath));
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(savePath));
                CompressorOutputStream cos = new CompressorStreamFactory().createCompressorOutputStream(type.name(), bos)
        ) {
            IOUtils.copy(bis, cos);
        }
    }

    @Override
    public void decompress(Path archivePath, Path savePath) throws IOException, CompressorException {
        try (
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(savePath));
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(archivePath));
                CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(type.name(), bis)
        ) {
            IOUtils.copy(cis, bos);
        }
    }
}
