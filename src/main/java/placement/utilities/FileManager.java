package placement.utilities;

import placement.exception.StorageException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Low-level file operations for data persistence.
 */
public class FileManager {
    public static String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    public static void writeFile(String path, String content) {
        try {
            Path p = Paths.get(path);
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            Files.write(p, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new StorageException("Failed to write file: " + path + " - " + e.getMessage());
        }
    }

    public static void appendToFile(String path, String content) {
        try {
            Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new StorageException("Failed to append to file: " + path);
        }
    }

    public static boolean fileExists(String path) { return Files.exists(Paths.get(path)); }

    public static void createDirectoryIfNotExists(String path) {
        try { Files.createDirectories(Paths.get(path)); }
        catch (IOException e) { throw new StorageException("Failed to create directory: " + path); }
    }

    public static void deleteFile(String path) {
        try { Files.deleteIfExists(Paths.get(path)); }
        catch (IOException e) { throw new StorageException("Failed to delete file: " + path); }
    }
}
