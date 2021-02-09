package com.example.pdpproject.models;

public abstract class Model {
    private String id;
    private String name;

    public Model(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


}
