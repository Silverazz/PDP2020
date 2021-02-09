package com.example.pdpproject.algorithms;

public class Item implements Comparable<Item>{
    private String name;
    private Float score;

    public Item(String name, Float score){
        this.name = name;
        this.score = score;
    }

    public String getName(){
        return name;
    }

    public Float getScore (){
        return score;
    }

    public void updateScore(Float score){
        this.score = score;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item otherItem = (Item)obj;
            boolean equals = this.getName().compareTo(otherItem.getName()) == 0;
        	return equals;
        } else {
            return false;
        }
   }
    
    @Override
    public int hashCode() {
    	return name.hashCode();
    }

	@Override
	public int compareTo(Item item) {
		return item.getScore().compareTo(score);
	}
}
