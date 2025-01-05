package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;

public class DPK15_impl_2 {

}
class Person2{
    private String name;
    private int age;
    private List<String> friends;

    public Person2(String name, int age){
        this.name = name;
        this.age = age;
        this.friends = new ArrayList<>();
    }

    public List<String> getFriends(){
        return friends;
    }

    public void addFriend(String name) {
        friends.add(name);
    }

    public String getName() {
        return  this.name;
    }

    public int getAge(){
        return this.age;
    }
}