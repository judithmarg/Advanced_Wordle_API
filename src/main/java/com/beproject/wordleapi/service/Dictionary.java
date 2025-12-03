package com.beproject.wordleapi.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
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
