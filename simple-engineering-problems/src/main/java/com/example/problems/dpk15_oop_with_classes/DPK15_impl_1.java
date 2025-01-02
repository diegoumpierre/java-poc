package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DPK15_impl_1 {


    public String moreFriends(List<Person> personList) {

        String winner = "";
        int maxFriends = -1;

        for (Person person : personList) {
            if (maxFriends < person.getFriends().size()) {
                maxFriends = person.getFriends().size();
                winner = person.getName();
            }
        }


        return winner;
    }
}

class Person {

    String name;
    int age;
    List<Friend> friends = new ArrayList<>();

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void addFriends(String name) {
        friends.add(new Friend(name));
    }

    public List<String> getFriends() {
        return friends.stream().map(Friend::getName).collect(Collectors.toList());
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void removeFriend(String name) {
        friends.remove(new Friend(name));
    }
}

class Friend {

    String name;

    public Friend(String name){
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this.getName() == obj.getName()){
            return true;
        }
        return false;
    }
}
