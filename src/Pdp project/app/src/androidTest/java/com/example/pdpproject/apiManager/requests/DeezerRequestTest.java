package com.example.pdpproject.apiManager.requests;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.apiManager.ExportCallbackO;
import com.example.pdpproject.repo.Singleton;
import com.example.pdpproject.service.ServiceDeezer;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

public class DeezerRequestTest {
    String token = "frYCE7wdTziNbfPQ5LnSCS4xBg2rJv3pLzWxubM0XXyUeeOt8N";
    Singleton singleton = Singleton.getInstance();
    DeezerRequest request = new DeezerRequest();
    @Before
    public void init(){
        singleton.setMainAccessToken(token);
        request.setToken(token);
    }

    @Test
    public void requestUserProfile() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestUserProfile(new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals("3496062564",data.getString("id"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestFollowedArtist() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestFollowedArtist(20, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals(4,data.getJSONArray("data").length());
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestPlaylistsByUser() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestPlaylistsByUser("3496062564", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals(4,data.getJSONArray("data").length());
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestArtistById() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestArtistById("4050205", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals("The Weeknd",data.getString("name"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestAlbumsFromArtist() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestAlbumsFromArtist("4050205", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals(24,data.getInt("total"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestTopTracksFromArtist() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestTopTracksFromArtist("4050205", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals(98,data.getInt("total"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestTracksByPlaylist() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestTracksByPlaylist("7342008944", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals(51,data.getInt("total"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestTracksFromAlbum() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestTracksFromAlbum("137371322", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals(19,data.getInt("total"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void requestTrack() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        request.requestTrack("909347642", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertEquals("Too Late",data.getString("title"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }

    @Test
    public void createAddDeletePlaylist() throws InterruptedException {
        final String[] id = new String[1];
        Semaphore semaphore = new Semaphore(0);
        request.requestCreatePlaylist("requestTest", new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertNotNull(data.getString("id"));
                    id[0] = data.getString("id");
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();

        String songs ="1575269,1575282";
        request.requestAddTracksToAppPlaylist(id[0], songs,new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertTrue(data.getBoolean("res"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();

        request.requestDeleteTracks(id[0], songs,new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertTrue(data.getBoolean("res"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();

        request.requestDeletePlaylist(id[0],new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    assertTrue(data.getBoolean("res"));
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
    }



}