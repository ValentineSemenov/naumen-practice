package ru.semenovValentine.dotabuff.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface IFileReader {
    List<String[]> readAllLines(Path filePath) throws Exception;
}
