package ru.semenovValentine.dotabuff.dataProcessors;

import ru.semenovValentine.dotabuff.interfaces.IFileReader;

import java.nio.file.Path;
import java.util.List;

public class DataIngestionManager {

    private final IFileReader fileReader;

    public DataIngestionManager(IFileReader fileReader) {
        this.fileReader = fileReader;
    }

    public List<String[]> readFromFile(Path path) throws Exception {
        return fileReader.readAllLines(path);
    }
}
