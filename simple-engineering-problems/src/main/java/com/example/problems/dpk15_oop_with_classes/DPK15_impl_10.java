package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_10 {

    public String moreFriends(List<Person10> lst) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger moreFriends = new AtomicInteger(Integer.MIN_VALUE);
        lst.stream().forEach(person10 -> {
            if (person10.getFriends().size() > moreFriends.get()) {
                moreFriends.set(person10.getFriends().size());
                winner.set(person10.getName());
            }
        });
        return winner.get();
    }

    public String lessFriends(List<Person10> lst) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger lessFriends = new AtomicInteger(Integer.MAX_VALUE);
        lst.stream().forEach(person10 -> {
            if(person10.getFriends().size() < lessFriends.get()){
                lessFriends.set(person10.getFriends().size());
                winner.set(person10.getName());
            }
        });
        return winner.get();
    }

    public String oldestFriends(List<Person10> lst) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger oldestFriends = new AtomicInteger(Integer.MIN_VALUE);
        lst.stream().forEach(person10 -> {
            person10.getFriends().stream().forEach(friend10 -> {
                if (friend10.getAge() > oldestFriends.get()){
                    oldestFriends.set(friend10.getAge());
                    winner.set(friend10.getName());
                }
            });
        });
        return winner.get();
    }
}

class Person10 {
    private String name;
    private int age;
    private List<Friend10> friend10List = new ArrayList<>();

    public Person10(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public List<Friend10> getFriends() {
        return friend10List;
    }

    public void addFriend(String name) {
        addFriend(name, 0);
    }

    public void addFriend(String name, int age) {
        Optional<Friend10> existFriend = friend10List.stream().filter(friend10 -> friend10.getName().equals(name)).findFirst();
        if (!existFriend.isPresent()) {
            friend10List.add(new Friend10(name, age));
        }
    }

    public void removeFriend(String name) {
        Optional<Friend10> existFriend = friend10List.stream().filter(friend10 -> friend10.getName().equals(name)).findFirst();
        if (existFriend.isPresent()) {
            friend10List.remove(existFriend.get());
        }
    }
}

class Friend10 {
    private String name;
    private int age;

    public Friend10(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Friend10(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}