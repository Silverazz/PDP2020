package com.example.pdpproject.algorithms;

import java.util.ArrayList;
import java.util.Collections;

public class BordaCount implements AlgorithmsInterface {

	public ArrayList<Item> algorithm(ArrayList<ArrayList<Item>> usersScores, ArrayList<Item> allArtists){
		 for(ArrayList<Item> itemArrayList : usersScores){
			 Collections.sort(itemArrayList);
			 int size = itemArrayList.size();
			 for(int i = 0; i < size/2; i++) {
				 Item tmp = itemArrayList.get(i);
				 itemArrayList.set(i, itemArrayList.get(size - i - 1));
				 itemArrayList.set(size - i - 1, tmp);
			 }
			 int begin_index = 0;
			 int same_score = 1;
			 float begin_score = itemArrayList.get(0).getScore();
			 float new_score = 0.0f;
			 for(int i = 1; i < size + 1; i++) {
				 if(i < size && Math.round((itemArrayList.get(i).getScore()*10)) == Math.round(begin_score*10)) {
					 same_score++;
					 new_score += i;
				 }
				 else {
					 for(int j = begin_index; j < i; j++) {
						 itemArrayList.get(j).updateScore(new_score/same_score);
					 }
					 if(i < size) {
						 begin_index = i;
						 same_score = 1;
						 begin_score = itemArrayList.get(i).getScore();
						 new_score = i; 
					 }
				 }
			 }
		 }
		
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
