package com.example.problems.dpk15_oop_with_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DPK15_impl_7 {
    public String moreFriends(List<Person7> person7List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger friends = new AtomicInteger(Integer.MIN_VALUE);
        person7List.stream().forEach(person7 -> {
            if (person7.getFriends().size() > friends.get()) {
                friends.set(person7.getFriends().size());
                winner.set(person7.getName());
            }
        });
        return winner.get();
    }

    public String lessFriends(List<Person7> person7List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger friends = new AtomicInteger(Integer.MAX_VALUE);
        person7List.stream().forEach(person7 -> {
            if (friends.get() > person7.getFriends().size()) {
                friends.set(person7.getFriends().size());
                winner.set(person7.getName());
            }
        });
        return winner.get();
    }

    public String oldestFriends(List<Person7> person7List) {
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger older = new AtomicInteger(Integer.MIN_VALUE);
        person7List.stream().forEach(person7 -> {
            person7.getFriends().stream().forEach(friend7 -> {
                if (friend7.getAge() > older.get()) {
                    older.set(friend7.getAge());
                    winner.set(friend7.getName());
                }
            });

        });
        return winner.get();
    }
}

class Person7 {
    private String name;
    private int age;
    private List<Friend7> friend7List = new ArrayList<>();

    public Person7(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<Friend7> getFriends() {
        return friend7List;
    }

    public void addFriend(String name) {
        addFriend(name, 0);
    }

    public void addFriend(String name, int age) {
        Optional<Friend7> friend7 = friend7List.stream().filter(friend -> friend.getName().equals(name)).findFirst();
        if (!friend7.isPresent()) {
            friend7List.add(new Friend7(name, age));
        }
    }

    public void removeFriend(String name) {
        Optional<Friend7> friend7 = friend7List.stream().filter(friend -> friend.getName().equals(name)).findFirst();
        if (friend7.isPresent()) {
            friend7List.remove(friend7.get());
        }

    }
}

class Friend7 {
    private String name;
    private int age;

    public Friend7(String name) {
        this.name = name;
    }

    public Friend7(String name, int age) {
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