package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_3 {

    public String moreFriends(List<Person3> person3List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger maxFriends = new AtomicInteger(Integer.MIN_VALUE);

        person3List.stream().forEach(person3 -> {
            if (maxFriends.get() < person3.getFriends().size()) {
                maxFriends.set(person3.getFriends().size());
                winner.set(person3.getName());
            }
        });
        return winner.get();
    }

    public String lessFriends(List<Person3> person3List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger lessFriends = new AtomicInteger(Integer.MAX_VALUE);

        person3List.stream().forEach(person3 -> {
            if (lessFriends.get() > person3.getFriends().size()) {
                lessFriends.set(person3.getFriends().size());
                winner.set(person3.getName());
            }
        });
        return winner.get();
    }

    public String oldestFriends(List<Person3> person3List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger oldestFriend = new AtomicInteger(Integer.MIN_VALUE);

        person3List.stream().forEach(person3 -> {
            person3.getFriends().stream().forEach(friend3 -> {
                if (oldestFriend.get() < friend3.getAge()) {
                    oldestFriend.set(friend3.getAge());
                    winner.set(friend3.getName());
                }
            });
        });
        return winner.get();
    }
}

class Person3 {

    private String name;
    private int age;
    private List<Friend3> friend3List = new ArrayList<>();

    public Person3(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void addFriend(String name) {
        addFriend(name,0);
    }
    public void addFriend(String name, int age) {
        Optional<Friend3> existFriend = friend3List.stream().filter(friend3 -> friend3.getName().equals(name)).findFirst();
        if (!existFriend.isPresent()) {
            friend3List.add(new Friend3(name, age));
        }
    }


    public List<Friend3> getFriends() {
        return friend3List;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void removeFriend(String name) {
        Optional<Friend3> existFriend = friend3List.stream().filter(friend3 -> friend3.getName().equals(name)).findFirst();
        if (existFriend.isPresent()) {
            friend3List.remove(existFriend.get());
        }
    }
}

class Friend3 {
    private String name;
    private int age;

    public Friend3(String name) {
        this.name = name;
    }

    public Friend3(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}