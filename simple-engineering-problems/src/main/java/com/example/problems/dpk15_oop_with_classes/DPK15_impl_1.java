package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;

public class DPK15_impl_1 {


}

class Person{

    String name;
    int age;
    List<String> friends = new ArrayList<>();

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void addFriends(String friend){
        friends.add(friend);
    }

    public List<String> getFriends() {
        return friends;
    }

    public int getAge(){
        return age;
    }

    public String getName() {
        return name;
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }
}
