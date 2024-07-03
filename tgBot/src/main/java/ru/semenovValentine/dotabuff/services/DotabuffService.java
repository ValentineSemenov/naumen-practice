package ru.semenovValentine.dotabuff.services;

import ru.semenovValentine.dotabuff.dataProcessors.DataIngestionManager;
import ru.semenovValentine.dotabuff.interfaces.IDotabuffService;

import java.nio.file.Path;
import java.util.*;

public class DotabuffService implements IDotabuffService {

    public void appLogic(Path path, DataIngestionManager dataProcessor, int n) {
        List<String[]> data;
        try {
            data = dataProcessor.readFromFile(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Структура для хранения суммарного количества игр за каждого героя
        Map<Integer, Integer> heroGames = calculateHeroGames(data);
        // Структура для хранения игр каждого пользователя за каждого героя
        Map<Integer, Map<Integer, Integer>> userHeroGames = calculateUserHeroGames(data);
        // Сортировка героев по количеству игр
        List<Map.Entry<Integer, Integer>> sortedHeroes = sortHeroesByGames(heroGames);
        // Выводит топ n героев
        printTopHeroes(sortedHeroes, userHeroGames, n);
    }

    // Метод для подсчета количества игр за каждого героя в целом
    public Map<Integer, Integer> calculateHeroGames(List<String[]> data) {
        Map<Integer, Integer> heroGames = new HashMap<>();
        for (String[] entry : data) {
            int heroId = Integer.parseInt(entry[1].trim());
            int numGames = Integer.parseInt(entry[2].trim());
            heroGames.put(heroId, heroGames.getOrDefault(heroId, 0) + numGames);
        }
        return heroGames;
    }

    // Метод для подсчета игр каждого пользователя за героя
    public Map<Integer, Map<Integer, Integer>> calculateUserHeroGames(List<String[]> data) {
        Map<Integer, Map<Integer, Integer>> userHeroGames = new HashMap<>();
        for (String[] entry : data) {
            int userId = Integer.parseInt(entry[0].trim());
            int heroId = Integer.parseInt(entry[1].trim());
            int numGames = Integer.parseInt(entry[2].trim());

            userHeroGames.putIfAbsent(heroId, new HashMap<>());
            Map<Integer, Integer> userGames = userHeroGames.get(heroId);
            userGames.put(userId, userGames.getOrDefault(userId, 0) + numGames);
        }
        return userHeroGames;
    }

    // Метод для сортировки героев по количеству игр
    public List<Map.Entry<Integer, Integer>> sortHeroesByGames(Map<Integer, Integer> heroGames) {
        List<Map.Entry<Integer, Integer>> sortedHeroes = new LinkedList<>(heroGames.entrySet());
        sortedHeroes.sort((h1, h2) -> Integer.compare(h2.getValue(), h1.getValue()));
        return sortedHeroes;
    }

    private void printTopHeroes(List<Map.Entry<Integer, Integer>> sortedHeroes, Map<Integer, Map<Integer, Integer>> userHeroGames, int n) {
        for (int i = 0; i < Math.min(n, sortedHeroes.size()); i++) {
            int heroId = sortedHeroes.get(i).getKey();
            int totalGames = sortedHeroes.get(i).getValue();
            int topUserId = Collections.max(userHeroGames.get(heroId).entrySet(), Map.Entry.comparingByValue()).getKey();
            System.out.printf("| Hero Id %d | Games played %d | User Id %d |%n", heroId, totalGames, topUserId);
        }
    }

}
