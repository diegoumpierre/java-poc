package com.example.problems.dpk15_oop_with_classes;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_4 {

    public String moreFriends(List<Person4> person4List) {
        AtomicReference<String> winner = new AtomicReference<>();

        AtomicInteger maxFriends = new AtomicInteger(Integer.MIN_VALUE);

        person4List.stream().forEach(person4 -> {
            if (maxFriends.get() < person4.getFriends().size()) {
                maxFriends.set(person4.getFriends().size());
                winner.set(person4.getName());
            }
        });
        return winner.get();
    }

    public String lessFriends(List<Person4> person4List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger lessFriends = new AtomicInteger(Integer.MAX_VALUE);

        person4List.stream().forEach(person4 -> {
            if (lessFriends.get() > person4.getFriends().size()) {
                lessFriends.set(person4.getFriends().size());
                winner.set(person4.getName());
            }
        });
        return winner.get();
    }

    public String oldestFriends(List<Person4> person4List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger oldestFriend = new AtomicInteger(Integer.MIN_VALUE);

        person4List.stream().forEach(person4 -> {
            person4.getFriends().stream().forEach(friend4 -> {
                if (oldestFriend.get() < friend4.getAge()) {
                    oldestFriend.set(friend4.getAge());
                    winner.set(friend4.getName());
                }
            });
        });
        return winner.get();
    }
}

class Person4 {
    String name;
    int age;
    List<Friend4> friend4List = new ArrayList<>();


    public Person4(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person4(String name) {
        new Person4(name, 0);
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void addFriend(String name) {

        Optional<Friend4> existFriend = friend4List.stream().filter(friend4 -> friend4.getName().equals(name)).findFirst();

        if (!existFriend.isPresent()) {
            friend4List.add(new Friend4(name, age));
        }

    }

    public void addFriend(String name, int age) {
        Optional<Friend4> existFriend = friend4List.stream().filter(friend4 -> friend4.getName().equals(name)).findFirst();
        if (!existFriend.isPresent()) {
            friend4List.add(new Friend4(name, age));
        }
    }

    public List<Friend4> getFriends() {
        return friend4List;
    }

    public void removeFriend(String name) {
        Optional<Friend4> existFriend = friend4List.stream().filter(friend4 -> friend4.getName().equals(name)).findFirst();
        if (existFriend.isPresent()) {
            friend4List.remove(existFriend.get());
        }
    }
}

class Friend4 {
    String name;
    int age;

    public Friend4(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }
}