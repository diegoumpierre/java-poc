package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_8 {

    public String moreFriends(List<Person8> person8List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger moreFriends = new AtomicInteger(Integer.MIN_VALUE);
        person8List.stream().forEach(person8 -> {
            if (person8.getFriends().size() > moreFriends.get()){
                moreFriends.set(person8.getFriends().size());
                winner.set(person8.getName());
            }
        });

        return winner.get();
    }

    public String lessFriends(List<Person8> person8List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger lessFriends = new AtomicInteger(Integer.MAX_VALUE);
        person8List.stream().forEach(person8 -> {
            if (person8.getFriends().size() < lessFriends.get()){
                lessFriends.set(person8.getFriends().size());
                winner.set(person8.getName());
            }
        });

        return winner.get();
    }

    public String oldestFriends(List<Person8> person8List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger oldestFriends = new AtomicInteger(Integer.MIN_VALUE);
        person8List.stream().forEach(person8 -> {

            person8.getFriends().stream().forEach(friend8 -> {
                if(friend8.getAge() > oldestFriends.get()){
                    oldestFriends.set(friend8.getAge());
                    winner.set(friend8.getName());
                }
            });
        });

        return winner.get();
    }
}

class Person8 {
    private String name;
    private int age;
    private List<Friend8> friend8List = new ArrayList<>();

    public Person8(String name, int age) {
        this.name = name;
        this.age = age;
    }


    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public List<Friend8> getFriends() {
        return friend8List;
    }

    public void addFriend(String name) {
        addFriend(name,0);
    }

    public void addFriend(String name, int age){
        Optional<Friend8> findFriend = friend8List.stream().filter(friend8 -> friend8.getName().equals(name)).findFirst();
        if(!findFriend.isPresent()){
            friend8List.add(new Friend8(name, age));
        }
    }


    public void removeFriend(String name) {
        Optional<Friend8> findFriend = friend8List.stream().filter(friend8 -> friend8.getName().equals(name)).findFirst();
        if(findFriend.isPresent()){
            friend8List.remove(findFriend.get());
        }
    }
}

class Friend8 {
    private String name;
    private int age;

    public Friend8(String name, int age) {
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