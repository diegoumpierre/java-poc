package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_5 {

    public String moreFriends(List<Person5> person5List) {
        AtomicReference<String> winnerName = new AtomicReference<>();
        AtomicInteger friends = new AtomicInteger(Integer.MIN_VALUE);

        person5List.stream().forEach(person5 -> {
            if (friends.get() < person5.getFriend5List().size()) {
                friends.set(person5.getFriend5List().size());
                winnerName.set(person5.getName());
            }
        });


        return winnerName.get();
    }

    public String lessFriends(List<Person5> person5List) {
        AtomicReference<String> winnerName = new AtomicReference<>();
        AtomicInteger friends = new AtomicInteger(Integer.MAX_VALUE);

        person5List.stream().forEach(person5 -> {
            if (friends.get() > person5.getFriend5List().size()) {
                friends.set(person5.getFriend5List().size());
                winnerName.set(person5.getName());
            }
        });


        return winnerName.get();
    }

    public String oldestFriends(List<Person5> person5List) {
        AtomicReference<String> winnerName = new AtomicReference<>();
        AtomicInteger oldest = new AtomicInteger(Integer.MIN_VALUE);

        person5List.stream().forEach(person5 -> {
            person5.getFriends().stream().forEach(friend5 -> {
                if (oldest.get() < friend5.getAge()) {
                    oldest.set(friend5.getAge());
                    winnerName.set(friend5.getName());
                }
            });
        });
        return winnerName.get();
    }
}

class Person5 {
    private String name;
    private int age;
    List<Friend5> friend5List = new ArrayList<>();

    public Person5(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<Friend5> getFriend5List() {
        return friend5List;
    }

    public void addFriend(String name) {
        addFriend(name, 0);
    }

    public void addFriend(String name, int age) {
        Optional<Friend5> friend5 = friend5List.stream().filter(friend -> friend.getName().equals(name)).findFirst();
        if (!friend5.isPresent()) {
            friend5List.add(new Friend5(name, age));
        }
    }

    public List<Friend5> getFriends() {
        return friend5List;
    }

    public void removeFriend(String name) {
        Optional<Friend5> friend5 = friend5List.stream().filter(friend -> friend.getName().equals(name)).findFirst();
        if (friend5.isPresent()) {
            friend5List.remove(friend5.get());
        }
    }
}

class Friend5 {
    private String name;
    private int age;

    public Friend5(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Friend5(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}