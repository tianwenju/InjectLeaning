package com.wenjutian.injectleaning.refect;

/**
 * Created by V.Wenju.Tian on 2016/11/24.
 */

public class SuperPeople implements Ishop{
    public  int age;
    private String address;
    @Override
    public void shop() {

    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    void eat(){
        System.out.println("eatting something");
    };
}
