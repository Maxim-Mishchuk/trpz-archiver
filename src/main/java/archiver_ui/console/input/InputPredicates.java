package archiver_ui.console.input;

import archiver_api.supported_types.ArchiveType;
import archiver_api.supported_types.CompressorType;
import org.apache.commons.compress.utils.FileNameUtils;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Predicate;

public class InputPredicates {
    public static final Predicate<String> isFileName = s -> s.matches("^[^/\\\\:*?\"<>|]+");
    public static final Predicate<String> isEntryName = s -> s.matches("^[^:*?\"<>|]+");
    public static final Predicate<String> isArchiveType = s -> {
        try {
            ArchiveType.valueOf(s.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    };

    public static final Predicate<String> isCompressorType = s -> {
        if (s.isBlank()) {
            return true;
        }
        try {
            CompressorType.valueOf(s.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    };

    public static final Predicate<String> isExistedPath = s -> {
        try {
            Path p = Path.of(s);
            return Files.exists(p);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    };

    public static final Predicate<String> isExistedDirectory = s -> {
        try {
            Path p = Path.of(s);
            return Files.isDirectory(p);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    };

    public static final Predicate<String> isExistedFile = s -> {
        try {
            Path p = Path.of(s);
            return Files.isRegularFile(p);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    };

    public static final Predicate<String> isCorrectArchivePath = s -> isExistedPath.test(s) && (isArchiveType.test(FileNameUtils.getExtension(s)) || isCompressorType.test(FileNameUtils.getExtension(s)));

    public static final Predicate<String> isCorrectIp = s -> s.matches("^(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
}
