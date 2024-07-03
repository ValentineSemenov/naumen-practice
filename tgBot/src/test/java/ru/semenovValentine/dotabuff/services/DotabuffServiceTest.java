package ru.semenovValentine.dotabuff.services;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import ru.semenovValentine.dotabuff.interfaces.IDotabuffService;

import java.util.*;


class DotabuffServiceTest {

    @Test
    void calculateHeroGames() {
        List<String[]> data = new ArrayList<>(
                Arrays.asList(
                        new String[] {"1", "120", "10"},
                        new String[] {"2", "100", "20"},
                        new String[] {"2", "100", "30"}
                )
        );
        IDotabuffService service = new DotabuffService();

        service.calculateHeroGames(data);

        Map<Integer, Integer> expectedResult = new HashMap<>();
        expectedResult.put(100, 50);
        expectedResult.put(120, 10);

        Map<Integer, Integer> actualResult = service.calculateHeroGames(data);

        Assert.isTrue(actualResult.equals(expectedResult), "Ожидаемый результат отличается от полученного");
    }

    @Test
    void calculateUserHeroGames(){
        List<String[]> data = new ArrayList<>(
                Arrays.asList(
                        new String[] {"1", "120", "10"},
                        new String[] {"2", "100", "20"},
                        new String[] {"2", "100", "30"}
                )
        );
        IDotabuffService service = new DotabuffService();

        Map<Integer, Map<Integer, Integer>> expectedResult = new HashMap<>();

        Map<Integer, Integer> innerMap1 = new HashMap<>();
        innerMap1.put(2, 50);
        expectedResult.put(100, innerMap1);

        Map<Integer, Integer> innerMap2 = new HashMap<>();
        innerMap2.put(1, 10);
        expectedResult.put(120, innerMap2);

        Map<Integer, Map<Integer, Integer>> actualResult = service.calculateUserHeroGames(data);
        Assert.isTrue(actualResult.equals(expectedResult), "Ожидаемый результат отличается от полученного");
    }
}