package com.beproject.wordleapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dictionary {
    private final ArrayList<String> manyWords;

    public Dictionary() {
        manyWords = new ArrayList<>();
        manyWords.addAll(List.of("avion", "novel", "water", "gemas", "house", "osito","libre", "month", "again"));
    }

    public String getRandomWord() {
        int random = new Random().nextInt(manyWords.size());
        return manyWords.get(random);
    }
}
