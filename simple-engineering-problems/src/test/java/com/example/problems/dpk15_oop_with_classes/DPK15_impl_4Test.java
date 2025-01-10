package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK15_impl_4Test {
    @Test
    void personCreationShouldBeSuccess() {
        Person4 person4 = new Person4("John", 30);
        person4.addFriend("Paul");
        person4.addFriend("George");
        person4.addFriend("Ringo");
        assertEquals(3, person4.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess() {
        Person4 person4 = new Person4("John", 30);
        assertEquals("John", person4.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess() {
        Person4 person4 = new Person4("John", 30);
        person4.addFriend("Paul");
        person4.addFriend("George");
        person4.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess() {
        Person4 person4 = new Person4("John", 30);
        assertEquals(30, person4.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess() {
        Person4 person4 = new Person4("John", 30);
        person4.addFriend("Paul");
        person4.addFriend("Paul");

        assertEquals(1, person4.getFriends().size());
        assertEquals("Paul", person4.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess() {
        Person4 person4 = new Person4("John", 30);
        person4.addFriend("Paul");
        person4.addFriend("George");
        person4.addFriend("Ringo");

        List<Friend4> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend4("Paul"));
        expectedFriends.add(new Friend4("Ringo"));

        person4.removeFriend("George");
        assertEquals(2, person4.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person4.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person4> person4List = new ArrayList<>();

        Person4 person4 = new Person4("John", 30);
        person4.addFriend("Paul");
        person4.addFriend("George");
        person4List.add(person4);

        person4 = new Person4("Ringo", 89);
        person4.addFriend("Paul");
        person4.addFriend("George");
        person4.addFriend("Ringo");
        person4.addFriend("Ringo1");
        person4.addFriend("Ringo2");
        person4.addFriend("Ringo3");
        person4List.add(person4);

        person4 = new Person4("John1", 67);
        person4.addFriend("Paul");
        person4List.add(person4);


        DPK15_impl_4 dpk15Impl4 = new DPK15_impl_4();
        assertEquals("Ringo", dpk15Impl4.moreFriends(person4List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person4> person4List = new ArrayList<>();

        Person4 person4 = new Person4("John", 30);
        person4.addFriend("Paul");
        person4.addFriend("George");
        person4List.add(person4);

        person4 = new Person4("Ringo", 89);
        person4.addFriend("Paul");
        person4.addFriend("George");
        person4.addFriend("Ringo2");
        person4.addFriend("Ringo");
        person4.addFriend("Ringo1");
        person4.addFriend("Ringo3");
        person4List.add(person4);

        person4 = new Person4("John1", 67);
        person4.addFriend("Paul");
        person4List.add(person4);


        DPK15_impl_4 dpk15Impl4 = new DPK15_impl_4();
        assertEquals("John1", dpk15Impl4.lessFriends(person4List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person4> person4List = new ArrayList<>();

        Person4 person4 = new Person4("John", 30);
        person4.addFriend("Paul", 60);
        person4.addFriend("George", 61);
        person4List.add(person4);

        person4 = new Person4("Ringo", 89);
        person4.addFriend("Paul", 69);
        person4.addFriend("George", 70);
        person4.addFriend("Ringo", 10);
        person4.addFriend("Ringo1", 62);
        person4.addFriend("Ringo2", 64);
        person4.addFriend("Ringo3", 62);
        person4List.add(person4);

        person4 = new Person4("John1", 67);
        person4.addFriend("Paul", 20);
        person4List.add(person4);


        DPK15_impl_4 dpk15Impl4 = new DPK15_impl_4();
        assertEquals("George", dpk15Impl4.oldestFriends(person4List));
    }
}