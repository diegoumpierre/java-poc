package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_9 {

    public String moreFriends(List<Person9> person9List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger moreFriends = new AtomicInteger(Integer.MIN_VALUE);

        person9List.stream().forEach(person9 -> {
            if(person9.getFriends().size() > moreFriends.get()){
                moreFriends.set(person9.getFriends().size());
                winner.set(person9.getName());
            }
        });


        return winner.get();
    }

    public String lessFriends(List<Person9> person9List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger lessFriends = new AtomicInteger(Integer.MAX_VALUE);

        person9List.stream().forEach(person9 -> {
            if(person9.getFriends().size() < lessFriends.get()){
                lessFriends.set(person9.getFriends().size());
                winner.set(person9.getName());
            }
        });


        return winner.get();
    }

    public String oldestFriends(List<Person9> person9List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger oldestFriend = new AtomicInteger(Integer.MIN_VALUE);

        person9List.stream().forEach(person9 -> {

            person9.getFriends().stream().forEach(friend9 -> {

                if(friend9.getAge() > oldestFriend.get()){
                    oldestFriend.set(friend9.getAge());
                    winner.set(friend9.getName());
                }

            });
        });
        return winner.get();


    }
}

class Person9 {
    private String name;
    private int age;
    private List<Friend9> friend9List = new ArrayList<>();

    public Person9(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public List<Friend9> getFriends() {
        return friend9List;
    }
    public void addFriend(String name){
        addFriend(name,0);
    }
    public void addFriend(String name, int age){
        Optional<Friend9> existFriend9 = friend9List.stream().filter(friend9 -> friend9.getName().equals(name)).findFirst();
        if(!existFriend9.isPresent()){
            friend9List.add(new Friend9(name, age));
        }
    }

    public void removeFriend(String name){
        Optional<Friend9> existFriend9 = friend9List.stream().filter(friend9 -> friend9.getName().equals(name)).findFirst();
        if(existFriend9.isPresent()){
            friend9List.remove(existFriend9.get());
        }
    }
}

class Friend9 {
    private String name;
    private int age;

    public Friend9(String name) {
        this.name = name;
    }

    public Friend9(String name, int age) {
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