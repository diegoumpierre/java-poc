package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_6 {

    public String moreFriends(List<Person6> person6List) {

        AtomicReference<String> winnerName = new AtomicReference<>();
        AtomicInteger friends = new AtomicInteger(Integer.MIN_VALUE);

        person6List.stream().forEach(person6 -> {
            if (friends.get() < person6.getFriend6List().size()) {
                friends.set(person6.getFriend6List().size());
                winnerName.set(person6.getName());
            }
        });

        return winnerName.get();
    }

    public String lessFriends(List<Person6> person6List) {
        AtomicReference<String> winnerName = new AtomicReference<>();
        AtomicInteger friends = new AtomicInteger(Integer.MAX_VALUE);

        person6List.stream().forEach(person6 -> {
            if (friends.get() > person6.getFriend6List().size()) {
                friends.set(person6.getFriend6List().size());
                winnerName.set(person6.getName());
            }
        });

        return winnerName.get();
    }

    public String oldestFriends(List<Person6> person6List) {
        AtomicReference<String> winnerName = new AtomicReference<>();
        AtomicInteger oldest = new AtomicInteger(Integer.MIN_VALUE);

        person6List.stream().forEach(person6 -> {
            person6.getFriends().stream().forEach(friend6 -> {
                if (oldest.get() < friend6.getAge()) {
                    oldest.set(friend6.getAge());
                    winnerName.set(friend6.getName());
                }
            });
        });
        return winnerName.get();
    }
}

class Person6 {
    private String name;
    private int age;
    private List<Friend6> friend6List = new ArrayList<>();

    public Person6(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<Friend6> getFriend6List() {
        return friend6List;
    }

    public void addFriend(String name) {
        addFriend(name,0);
    }

    public void addFriend(String name, int age) {
        Optional<Friend6> friend6 = friend6List.stream().filter(friend -> friend.getName().equals(name)).findFirst();
        if (!friend6.isPresent()) {
            friend6List.add(new Friend6(name, age));
        }
    }

    public List<Friend6> getFriends() {
        return friend6List;
    }

    public void removeFriend(String name) {
        Optional<Friend6> friend6 = friend6List.stream().filter(friend -> friend.getName().equals(name)).findFirst();
        if (friend6.isPresent()) {
            friend6List.remove(friend6.get());
        }
    }
}

class Friend6 {
    private String name;
    private int age;

    public Friend6(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Friend6(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public String getName() {
        return this.name;
    }
}