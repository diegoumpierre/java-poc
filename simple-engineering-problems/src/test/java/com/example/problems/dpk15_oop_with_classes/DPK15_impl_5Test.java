package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK15_impl_5Test {
    @Test
    void personCreationShouldBeSuccess() {
        Person5 person5 = new Person5("John", 30);
        person5.addFriend("Paul");
        person5.addFriend("George");
        person5.addFriend("Ringo");
        assertEquals(3, person5.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess() {
        Person5 person5 = new Person5("John", 30);
        assertEquals("John", person5.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess() {
        Person5 person5 = new Person5("John", 30);
        person5.addFriend("Paul");
        person5.addFriend("George");
        person5.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess() {
        Person5 person5 = new Person5("John", 30);
        assertEquals(30, person5.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess() {
        Person5 person5 = new Person5("John", 30);
        person5.addFriend("Paul");
        person5.addFriend("Paul");

        assertEquals(1, person5.getFriends().size());
        assertEquals("Paul", person5.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess() {
        Person5 person5 = new Person5("John", 30);
        person5.addFriend("Paul");
        person5.addFriend("George");
        person5.addFriend("Ringo");

        List<Friend5> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend5("Paul"));
        expectedFriends.add(new Friend5("Ringo"));

        person5.removeFriend("George");
        assertEquals(2, person5.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person5.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person5> person5List = new ArrayList<>();

        Person5 person5 = new Person5("John", 30);
        person5.addFriend("Paul");
        person5.addFriend("George");
        person5List.add(person5);

        person5 = new Person5("Ringo", 89);
        person5.addFriend("Paul");
        person5.addFriend("George");
        person5.addFriend("Ringo");
        person5.addFriend("Ringo1");
        person5.addFriend("Ringo2");
        person5.addFriend("Ringo3");
        person5List.add(person5);

        person5 = new Person5("John1", 67);
        person5.addFriend("Paul");
        person5List.add(person5);


        DPK15_impl_5 dpk15Impl5 = new DPK15_impl_5();
        assertEquals("Ringo", dpk15Impl5.moreFriends(person5List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person5> person5List = new ArrayList<>();

        Person5 person5 = new Person5("John", 30);
        person5.addFriend("Paul");
        person5.addFriend("George");
        person5List.add(person5);

        person5 = new Person5("Ringo", 89);
        person5.addFriend("Paul");
        person5.addFriend("George");
        person5.addFriend("Ringo2");
        person5.addFriend("Ringo");
        person5.addFriend("Ringo1");
        person5.addFriend("Ringo3");
        person5List.add(person5);

        person5 = new Person5("John1", 67);
        person5.addFriend("Paul");
        person5List.add(person5);


        DPK15_impl_5 dpk15Impl5 = new DPK15_impl_5();
        assertEquals("John1", dpk15Impl5.lessFriends(person5List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person5> person5List = new ArrayList<>();

        Person5 person5 = new Person5("John", 30);
        person5.addFriend("Paul", 60);
        person5.addFriend("George", 61);
        person5List.add(person5);

        person5 = new Person5("Ringo", 89);
        person5.addFriend("Paul", 69);
        person5.addFriend("George", 70);
        person5.addFriend("Ringo", 10);
        person5.addFriend("Ringo1", 62);
        person5.addFriend("Ringo2", 64);
        person5.addFriend("Ringo3", 62);
        person5List.add(person5);

        person5 = new Person5("John1", 67);
        person5.addFriend("Paul", 20);
        person5List.add(person5);


        DPK15_impl_5 dpk15Impl5 = new DPK15_impl_5();
        assertEquals("George", dpk15Impl5.oldestFriends(person5List));
    }
}