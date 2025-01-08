package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DPK15_impl_2 {

    public String moreFriends(List<Person2> person2List) {
        String name = null;
        int maxFriends = Integer.MIN_VALUE;

        for (Person2 person2 : person2List) {
            if (maxFriends < person2.getFriends().size()) {
                maxFriends = person2.getFriends().size();
                name = person2.getName();
            }
        }
        return name;
    }

    public String lessFriends(List<Person2> person2List) {
        String name = null;
        int minFriends = Integer.MAX_VALUE;
        for (Person2 person2 : person2List) {
            if (minFriends > person2.getFriends().size()) {
                minFriends = person2.getFriends().size();
                name = person2.getName();
            }
        }
        return name;
    }

    public String oldestFriends(List<Person2> person2List) {
        String name = null;
        int oldestFriend = Integer.MIN_VALUE;
        for (Person2 person2 : person2List) {
            for (Friend2 friend2 : person2.getFriends()) {
                if (oldestFriend < friend2.getAge()){
                    oldestFriend = friend2.getAge();
                    name = friend2.getName();
                }
            }
        }
        return name;
    }
}

class Person2 {
    private String name;
    private int age;
    private List<Friend2> friends;

    public Person2(String name, int age) {
        this.name = name;
        this.age = age;
        this.friends = new ArrayList<>();
    }


    public List<Friend2> getFriends() {
        return friends;
    }

    public void addFriend(String name) {
        boolean exists = false;
        for (Friend2 friend2 : friends) {
            if (friend2.getName().equals(name)) {
                exists = true;
            }
        }
        if (!exists) {
            friends.add(new Friend2(name));
        }
    }

    public void addFriend(String name, int age) {
        boolean exists = false;
        for (Friend2 friend2 : friends) {
            if (friend2.getName().equals(name)) {
                exists = true;
            }
        }
        if (!exists) {
            friends.add(new Friend2(name, age));
        }
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public void removeFriend(String name) {
        for (Friend2 friend2 : friends) {
            if (friend2.getName().equals(name)) {
                friends.remove(friend2);
            }
        }

    }
}

class Friend2 {
    private String name;
    private int age;

    public Friend2(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Friend2(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getAge(){
        return age;
    }
}