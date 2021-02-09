package com.example.pdpproject.models;
import com.example.pdpproject.models.modelInterface.UserModelItf;
import com.example.pdpproject.models.users.UserModel;


public class User extends UserModel implements UserModelItf {
    private UserModelItf userItf;

    public User(String id, String name, UserModelItf userItf) {
        super(id,name);
        this.userItf = userItf;
    }


    @Override
    public UserModelItf getMyModelItf() {
        return userItf;
    }
}
