package ru.semenovValentine.dotabuff.interfaces;

import java.util.List;
import java.util.Map;

public interface IDotabuffService {
    Map<Integer, Integer> calculateHeroGames(List<String[]> data);

    Map<Integer, Map<Integer, Integer>> calculateUserHeroGames(List<String[]> data);

    List<Map.Entry<Integer, Integer>> sortHeroesByGames(Map<Integer, Integer> heroGames);
}
