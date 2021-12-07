package ru.ilmira;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    private static final String filesDir = "./server/files";
    private static final Path dirPath = Paths.get(filesDir);

    public static List<Path> getFilePaths() throws IOException {
        return Files.list(dirPath)
                .map(Path::getFileName)
                .collect(Collectors.toList());
    }

    public static File getFile(String fileName) {
        Path path = Paths.get(dirPath.toFile().getPath(), fileName);
        return path.toFile();
    }
}