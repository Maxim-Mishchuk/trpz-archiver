package archiver_api.compressors;

import org.apache.commons.compress.compressors.CompressorException;

import java.io.IOException;
import java.nio.file.Path;

public interface ICompressor {
    void compress(Path tarArchive, Path savePath) throws IOException, CompressorException;
    void decompress(Path tarArchive, Path savePath) throws IOException, CompressorException;
}
