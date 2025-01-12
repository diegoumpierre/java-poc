package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK15_impl_6Test {



    @Test
    void personCreationShouldBeSuccess() {
        Person6 person6 = new Person6("John", 30);
        person6.addFriend("Paul");
        person6.addFriend("George");
        person6.addFriend("Ringo");
        assertEquals(3, person6.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess() {
        Person6 person6 = new Person6("John", 30);
        assertEquals("John", person6.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess() {
        Person6 person6 = new Person6("John", 30);
        person6.addFriend("Paul");
        person6.addFriend("George");
        person6.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess() {
        Person6 person6 = new Person6("John", 30);
        assertEquals(30, person6.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess() {
        Person6 person6 = new Person6("John", 30);
        person6.addFriend("Paul");
        person6.addFriend("Paul");

        assertEquals(1, person6.getFriends().size());
        assertEquals("Paul", person6.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess() {
        Person6 person6 = new Person6("John", 30);
        person6.addFriend("Paul");
        person6.addFriend("George");
        person6.addFriend("Ringo");

        List<Friend6> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend6("Paul"));
        expectedFriends.add(new Friend6("Ringo"));

        person6.removeFriend("George");
        assertEquals(2, person6.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person6.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person6> person6List = new ArrayList<>();

        Person6 person6 = new Person6("John", 30);
        person6.addFriend("Paul");
        person6.addFriend("George");
        person6List.add(person6);

        person6 = new Person6("Ringo", 89);
        person6.addFriend("Paul");
        person6.addFriend("George");
        person6.addFriend("Ringo");
        person6.addFriend("Ringo1");
        person6.addFriend("Ringo2");
        person6.addFriend("Ringo3");
        person6List.add(person6);

        person6 = new Person6("John1", 67);
        person6.addFriend("Paul");
        person6List.add(person6);


        DPK15_impl_6 dpk15Impl6 = new DPK15_impl_6();
        assertEquals("Ringo", dpk15Impl6.moreFriends(person6List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person6> person6List = new ArrayList<>();

        Person6 person6 = new Person6("John", 30);
        person6.addFriend("Paul");
        person6.addFriend("George");
        person6List.add(person6);

        person6 = new Person6("Ringo", 89);
        person6.addFriend("Paul");
        person6.addFriend("George");
        person6.addFriend("Ringo2");
        person6.addFriend("Ringo");
        person6.addFriend("Ringo1");
        person6.addFriend("Ringo3");
        person6List.add(person6);

        person6 = new Person6("John1", 67);
        person6.addFriend("Paul");
        person6List.add(person6);


        DPK15_impl_6 dpk15Impl6 = new DPK15_impl_6();
        assertEquals("John1", dpk15Impl6.lessFriends(person6List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person6> person6List = new ArrayList<>();

        Person6 person6 = new Person6("John", 30);
        person6.addFriend("Paul", 60);
        person6.addFriend("George", 61);
        person6List.add(person6);

        person6 = new Person6("Ringo", 89);
        person6.addFriend("Paul", 69);
        person6.addFriend("George", 70);
        person6.addFriend("Ringo", 10);
        person6.addFriend("Ringo1", 62);
        person6.addFriend("Ringo2", 64);
        person6.addFriend("Ringo3", 62);
        person6List.add(person6);

        person6 = new Person6("John1", 67);
        person6.addFriend("Paul", 20);
        person6List.add(person6);


        DPK15_impl_6 dpk15Impl6 = new DPK15_impl_6();
        assertEquals("George", dpk15Impl6.oldestFriends(person6List));
    }
    
    
    
    
}