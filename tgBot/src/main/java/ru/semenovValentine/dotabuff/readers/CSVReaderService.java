package ru.semenovValentine.dotabuff.readers;

import ru.semenovValentine.dotabuff.interfaces.IFileReader;

import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CSVReaderService implements IFileReader {
    @Override
    public List<String[]> readAllLines(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                csvReader.readNext(); // Пропускаем первую строку
                return csvReader.readAll();
            }
        }
    }
}
