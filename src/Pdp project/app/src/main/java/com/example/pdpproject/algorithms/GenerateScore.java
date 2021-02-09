package com.example.pdpproject.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.example.pdpproject.apiManager.APIManager;
import com.example.pdpproject.apiManager.APIManagerItf;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.User;
import com.example.pdpproject.repo.Singleton;

public class GenerateScore {
   // List of all artists from all users
    private ArrayList<Item> allArtists;
    private Singleton singleton = Singleton.getInstance();
    private APIManager apiManager;

    public GenerateScore(APIManagerItf apiManagerItf) {
        this.apiManager = new APIManager(apiManagerItf);
    }

    /*
     * Add all the artists in the list `allArtists`
    */
    private void addAllArtists(){
        for(User u : singleton.getAllUser()) {
            for(String artist : u.getArtistsId()){
                Item item = new Item(artist, 0.0f);
                if(! allArtists.contains(item)){
                	allArtists.add(item);
                }
            }
        }
    }

    /*
     * Returns the list of scores by artist for a user.
    */
    private ArrayList<Item> createScoresForAUser(User u){
        ArrayList<Item> userScores = new ArrayList<>();
        for(Item it : allArtists){
        	userScores.add(new Item(it.getName(), it.getScore()));
		}

        for(Item item : userScores){
			if(u.getFollowedArtistsIds().contains(item.getName())){
				item.updateScore(item.getScore() + 1.5f);
			}
            for(String s : u.getArtistsId()){
                if(s.equals(item.getName())){
                    item.updateScore(item.getScore() + 1.0f);
                }
            }
        }
        return userScores;
    }

    /*
     * Returns the list of scores by artist for all users.
    */
    private ArrayList<ArrayList<Item>> createScores(){
        ArrayList<ArrayList<Item>> usersScores = new ArrayList<>();
        for(User u : singleton.getAllUser()) {
            usersScores.add(createScoresForAUser(u));
        }
        return usersScores;
    }

    /*
     * Select the artists with the best scores. The number of artists
     * to select is proportional to `playlistSize` and to the total
     * number of artists.
    */
    private ArrayList<Item> createTopArtists(int playlistSize){
        ArrayList<Item> topArtists = new ArrayList<>();
        int nbUsers = singleton.getAllUser().size();
        int nbArtists;
        
        if(playlistSize < 15){
            nbArtists = (int) (0.5 * playlistSize);
        }
        else if(playlistSize >= 15 && playlistSize <= 50){
            nbArtists = (int) (0.25 * playlistSize);
        }
        else{
            nbArtists = (int) (0.1 * playlistSize);
        }

        if(nbArtists > 0.8 * allArtists.size()){
            nbArtists = (int) (0.8 * allArtists.size());
        }
        
        if(nbArtists < nbUsers) {
        	nbArtists = Math.min(nbUsers, allArtists.size());
        }

        for(int i = 0; i < nbArtists; i++){
        	if(i != allArtists.size()-1 && (int)(allArtists.get(i).getScore() * 100) != (int)(allArtists.get(i+1).getScore() * 100)) {
        		topArtists.add(allArtists.get(i));
        	}
        	else {
        		ArrayList<User> users = new ArrayList<>(singleton.getAllUser());
        		ArrayList<Item> itemWithSameScore = new ArrayList<Item>();
        		itemWithSameScore.add(allArtists.get(i));
        		int number = i+1;
        		while(number < allArtists.size() && (int) (allArtists.get(number).getScore() * 100) == (int) (allArtists.get(i).getScore() * 100)) {
        			itemWithSameScore.add(allArtists.get(number));
        			number++;
        		}
        		while(i < number && i < nbArtists) {
        			int index = (int) (Math.random() * itemWithSameScore.size());
        			Item it = itemWithSameScore.get(index);
        			Iterator<User> usersItr = users.iterator();
        			while(usersItr.hasNext()) {
        				User u = usersItr.next();
        				if(u.getArtistsId().contains(it.getName())) {
        					usersItr.remove();
        				}
        			}
        			itemWithSameScore.remove(index);
        			topArtists.add(it);
        			i++;
        		}
        		for(User u : users) {
        			for(Item it : itemWithSameScore) {
        				if(u.getArtistsId().contains(it.getName())) {
        					topArtists.add(it);
        					break;
        				}
        			}
        		}
        	}
        }
        return topArtists;
    }

    /*
     * Returns an HashMap where the key is the name of the artist and 
     * the value the number of tracks there will be in the final playlist
     * according to `playlistSize`.
    */
    private LinkedHashMap<String, Integer> generateNumberOfTracksPerArtist(int playlistSize){
        ArrayList<Item> topArtists = createTopArtists(playlistSize);
//        LogMsg.logAppdata("topArtists " + topArtists.size());
        LinkedHashMap<String, Integer> numberOfTracksPerArtist = new LinkedHashMap<String, Integer>();

        float scoreTotal = 0.0f;
        for(Item item : topArtists){
            scoreTotal += item.getScore();
        }
        int nbTracks = 0;
        for(Item item : topArtists){
            //LogMsg.logAppdata("scoreTotal " + (item.getScore() / scoreTotal) );
            int nb = Math.round((item.getScore() / scoreTotal) * playlistSize);
            nbTracks += nb;
            if(nbTracks > playlistSize) {
            	nb = nb - (nbTracks - playlistSize);
            	nbTracks = playlistSize;
            }
            numberOfTracksPerArtist.put(item.getName(), nb);
        }
        int i = 0;
        ArrayList<String> listKeys = new ArrayList<String>(numberOfTracksPerArtist.keySet());
        while(nbTracks + i < playlistSize) {
        	String key = listKeys.get(i % numberOfTracksPerArtist.size());
        	Integer newVal = numberOfTracksPerArtist.get(key)+1;
        	numberOfTracksPerArtist.put(key, newVal);
        	i++;
        }
        return numberOfTracksPerArtist;
    }

    /*
     * Returns the playlist created containing `playlistSize` tracks.
    */
    private ArrayList<Track> createPlaylist(int playlistSize){
        
        ArrayList<Track> playlist = new ArrayList<>();
        HashMap<String, Integer> numberOfTracksPerArtist = generateNumberOfTracksPerArtist(playlistSize);

        HashMap<String, Track> trackHashMap = singleton.getTrackHashMap();
        for (Map.Entry<String, Integer> entry : numberOfTracksPerArtist.entrySet()) {
            int numberOfTracks = 0;
            HashMap<String, Track> tracksOfTheArtist = new HashMap<String, Track>();
            for(Map.Entry<String, Track> e : trackHashMap.entrySet()){
                if(e.getValue().getArtistsIds().contains(entry.getKey())){
                    tracksOfTheArtist.put(e.getKey(), e.getValue());
                }
            }
            ArrayList<String> listKeys = new ArrayList<String>(tracksOfTheArtist.keySet());
            while (numberOfTracks < (int) (entry.getValue() / 2) && numberOfTracks < tracksOfTheArtist.size()) {
            	int numOfTrack = (int)(Math.random() * tracksOfTheArtist.size()); 
            	String key = listKeys.get(numOfTrack);
            	Track track = tracksOfTheArtist.get(key);
            	boolean filter = !track.getName().toLowerCase().contains("remix");
                filter = filter && !track.getName().toLowerCase().contains("mix");
                if (!playlist.contains(track) && filter) {
                    playlist.add(track);
                    numberOfTracks++;
                }
            }

            String artistId = entry.getKey();
            Set<String> albums = apiManager.fetchAlbumsIdsFromArtist(artistId,false);
            Iterator<String> itr = albums.iterator();
            ArrayList<Track> allTracksOfTheArtist = new ArrayList<Track>();
            while (itr.hasNext()){
                String albumId = (String) itr.next();
                ArrayList<Track> tracks =apiManager.exportTracksFromAlbum(albumId,false);
                if(tracks!=null)
                    allTracksOfTheArtist.addAll(tracks);
            }
            while (numberOfTracks < (int) (entry.getValue())){
            	int numOfTrack = (int)(Math.random() * allTracksOfTheArtist.size());
                Track track = allTracksOfTheArtist.get(numOfTrack);
            	boolean filter = !track.getName().toLowerCase().contains("remix");
                filter = filter && !track.getName().toLowerCase().contains("mix");
                if (!playlist.contains(track) && filter) {
                    playlist.add(track);
                    numberOfTracks++;
                }
            }
                //méthode à créer pour récupérer des tracks dans la plateforme grâce au nom de l'artiste
                // t = getTrackByArtist();
        }

//        while(playlist.size() < playlistSize){
            // appeler une méthode qui récupère des tracks d'artistes similaires
//        }
       return playlist;
     }

    /*
     * Returns a playlist of size `playlistSize` created from the algorithm `algo`.
    */
    public ArrayList<Track> generatePlaylist(AlgorithmsInterface algo, int playlistSize){
    	allArtists = new ArrayList<Item>();
    	addAllArtists();
		ArrayList<ArrayList<Item>> userScores = createScores();
		allArtists = algo.algorithm(userScores, allArtists);
		ArrayList<Track> playlist = createPlaylist(playlistSize);
		return playlist;
    }
}
