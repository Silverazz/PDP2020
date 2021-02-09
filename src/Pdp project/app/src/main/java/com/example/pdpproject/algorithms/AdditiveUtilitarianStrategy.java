package com.example.pdpproject.algorithms;

import java.util.ArrayList;
import java.util.Collections;

public class AdditiveUtilitarianStrategy implements AlgorithmsInterface {

	public ArrayList<Item> algorithm(ArrayList<ArrayList<Item>> usersScores, ArrayList<Item> allArtists){
        for(Item item : allArtists){
            for(ArrayList<Item> itemArrayList : usersScores){
                int index = itemArrayList.indexOf(item);
                Item it = itemArrayList.get(index);
                item.updateScore(item.getScore() + it.getScore());
            }
        }
        Collections.sort(allArtists);
        return allArtists;
    }
}
