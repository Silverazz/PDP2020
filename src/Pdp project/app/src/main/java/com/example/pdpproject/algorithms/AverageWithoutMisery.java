package com.example.pdpproject.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class AverageWithoutMisery implements AlgorithmsInterface {

	public ArrayList<Item> algorithm(ArrayList<ArrayList<Item>> usersScores, ArrayList<Item> allArtists){
        Iterator<Item> itemIterator = allArtists.iterator();
        while(itemIterator.hasNext()) {
        	Item item = itemIterator.next();
            for(ArrayList<Item> itemArrayList : usersScores){
                int index = itemArrayList.indexOf(item);
                Item it = itemArrayList.get(index);
                if(it.getScore() < 1) {
                	itemIterator.remove();
                	break;
                }
                item.updateScore(item.getScore() + it.getScore());
            }
        }
        Collections.sort(allArtists);
        return allArtists;
    }
}
