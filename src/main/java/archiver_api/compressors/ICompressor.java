package archiver_api.compressors;

import org.apache.commons.compress.compressors.CompressorException;

import java.io.IOException;
import java.nio.file.Path;

public interface ICompressor {
    void compress(Path archivePath, Path savePath) throws IOException, CompressorException;
    void decompress(Path archivePath, Path savePath) throws IOException, CompressorException;
}
