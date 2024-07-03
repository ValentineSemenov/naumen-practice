package ru.semenovValentine.dotabuff;

import ru.semenovValentine.dotabuff.dataProcessors.DataIngestionManager;
import ru.semenovValentine.dotabuff.interfaces.IFileReader;
import ru.semenovValentine.dotabuff.readers.CSVReaderService;
import ru.semenovValentine.dotabuff.services.DotabuffService;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestApplication {
    public static void main(String[] args) {
        int rowsToShow = Integer.parseInt(args[1]);
        Path filePath = Paths.get(args[0]);
        IFileReader reader = new CSVReaderService();
        DataIngestionManager dataManager = new DataIngestionManager(reader);
        DotabuffService dotabuffService = new DotabuffService();

        // Выполнение логики доп задания
        dotabuffService.appLogic(filePath, dataManager, rowsToShow);
    }
}

