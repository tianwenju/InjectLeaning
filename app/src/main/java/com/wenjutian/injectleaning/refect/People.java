package com.wenjutian.injectleaning.refect;

/**
 * Created by V.Wenju.Tian on 2016/11/18.
 */

@CAnnotation(name = "王五",age = 12)
public class People extends SuperPeople{

    @FAnnotaion(name = "xiaoming")
   public String name;
    private int id;

    public People() {
    }

    public People(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @MAnnotaion(name = "sdfsdf")
    public void setName(@PAnnotion(name = "hahah") String name) {
        this.name = name;
    }


    public void setName(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }
    private void buy(String something){

        System.out.println("调用buy----"+something);
    }


    @Override
    public void shop() {
        super.shop();
    }


}
