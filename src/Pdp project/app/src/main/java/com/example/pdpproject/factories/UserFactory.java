package com.example.pdpproject.factories;

import com.example.pdpproject.models.User;
import com.example.pdpproject.models.modelInterface.UserModelItf;

import org.json.JSONException;
import org.json.JSONObject;

public class UserFactory {

    public static User getUser(JSONObject UserInfos, UserModelItf userModel) {
        String id = null;
        String name = null;
        String platform = null;
        try {
            id = UserInfos.getString("id");
            name = UserInfos.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new User(id,name,userModel);
    }
}
