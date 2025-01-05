package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DPK15_impl_1 {


    public String moreFriends(List<Person1> person1List) {

        String winner = "";
        int maxFriends = Integer.MIN_VALUE;

        for (Person1 person1 : person1List) {
            if (maxFriends < person1.getFriends().size()) {
                maxFriends = person1.getFriends().size();
                winner = person1.getName();
            }
        }
        return winner;
    }

    public String lessFriends(List<Person1> person1List) {

        String winner = "";
        int minFriends = Integer.MAX_VALUE;

        for (Person1 person1 : person1List) {
            if (minFriends > person1.getFriends().size()) {
                minFriends = person1.getFriends().size();
                winner = person1.getName();
            }
        }
        return winner;
    }

    public String oldestFriends(List<Person1> person1List) {
        String winner = "";
        int oldestFriend = Integer.MIN_VALUE;

        for (Person1 person1 : person1List) {
            for(Friend friend : person1.friends){
                if (oldestFriend < friend.getAge()) {
                    oldestFriend = friend.getAge();
                    winner = friend.getName();
                }
            }
        }
        return winner;
    }
}

class Person1 {

    String name;
    int age;
    List<Friend> friends = new ArrayList<>();

    public Person1(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void addFriends(String name) {
        boolean exists = false;
        for (Friend friend : friends) {
            if (friend.name.equals(name)) {
                exists = true;
            }
        }
        if (!exists) {
            friends.add(new Friend(name));
        }
    }

    public void addFriends(String name, int age) {
        boolean exists = false;
        for (Friend friend : friends) {
            if (friend.name.equals(name)) {
                exists = true;
            }
        }
        if (!exists) {
            friends.add(new Friend(name, age));
        }
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
        for (Friend friend : friends) {
            if (friend.getName().equals(name)) {
                friends.remove(friend);
            }
        }
    }
}

class Friend {

    String name;
    int age;

    public Friend(String name) {
        this.name = name;
    }

    public Friend(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }


}
