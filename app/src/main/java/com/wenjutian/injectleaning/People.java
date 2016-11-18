package com.wenjutian.injectleaning;

/**
 * Created by V.Wenju.Tian on 2016/11/18.
 */

public class People {
    String name;
    int id;

    public People() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
