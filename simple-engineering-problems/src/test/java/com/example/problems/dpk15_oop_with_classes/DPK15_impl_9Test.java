package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK15_impl_9Test {

    @Test
    void personCreationShouldBeSuccess() {
        Person9 person9 = new Person9("John", 30);
        person9.addFriend("Paul");
        person9.addFriend("George");
        person9.addFriend("Ringo");
        assertEquals(3, person9.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess() {
        Person9 person9 = new Person9("John", 30);
        assertEquals("John", person9.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess() {
        Person9 person9 = new Person9("John", 30);
        person9.addFriend("Paul");
        person9.addFriend("George");
        person9.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess() {
        Person9 person9 = new Person9("John", 30);
        assertEquals(30, person9.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess() {
        Person9 person9 = new Person9("John", 30);
        person9.addFriend("Paul");
        person9.addFriend("Paul");

        assertEquals(1, person9.getFriends().size());
        assertEquals("Paul", person9.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess() {
        Person9 person9 = new Person9("John", 30);
        person9.addFriend("Paul");
        person9.addFriend("George");
        person9.addFriend("Ringo");

        List<Friend6> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend6("Paul"));
        expectedFriends.add(new Friend6("Ringo"));

        person9.removeFriend("George");
        assertEquals(2, person9.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person9.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person9> person9List = new ArrayList<>();

        Person9 person9 = new Person9("John", 30);
        person9.addFriend("Paul");
        person9.addFriend("George");
        person9List.add(person9);

        person9 = new Person9("Ringo", 89);
        person9.addFriend("Paul");
        person9.addFriend("George");
        person9.addFriend("Ringo");
        person9.addFriend("Ringo1");
        person9.addFriend("Ringo2");
        person9.addFriend("Ringo3");
        person9List.add(person9);

        person9 = new Person9("John1", 67);
        person9.addFriend("Paul");
        person9List.add(person9);


        DPK15_impl_9 dpk15Impl9 = new DPK15_impl_9();
        assertEquals("Ringo", dpk15Impl9.moreFriends(person9List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person9> person9List = new ArrayList<>();

        Person9 person9 = new Person9("John", 30);
        person9.addFriend("Paul");
        person9.addFriend("George");
        person9List.add(person9);

        person9 = new Person9("Ringo", 89);
        person9.addFriend("Paul");
        person9.addFriend("George");
        person9.addFriend("Ringo2");
        person9.addFriend("Ringo");
        person9.addFriend("Ringo1");
        person9.addFriend("Ringo3");
        person9List.add(person9);

        person9 = new Person9("John1", 67);
        person9.addFriend("Paul");
        person9List.add(person9);


        DPK15_impl_9 dpk15Impl9 = new DPK15_impl_9();
        assertEquals("John1", dpk15Impl9.lessFriends(person9List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person9> person9List = new ArrayList<>();

        Person9 person9 = new Person9("John", 30);
        person9.addFriend("Paul", 60);
        person9.addFriend("George", 61);
        person9List.add(person9);

        person9 = new Person9("Ringo", 89);
        person9.addFriend("Paul", 69);
        person9.addFriend("George", 70);
        person9.addFriend("Ringo", 10);
        person9.addFriend("Ringo1", 62);
        person9.addFriend("Ringo2", 64);
        person9.addFriend("Ringo3", 62);
        person9List.add(person9);

        person9 = new Person9("John1", 67);
        person9.addFriend("Paul", 20);
        person9List.add(person9);


        DPK15_impl_9 dpk15Impl9 = new DPK15_impl_9();
        assertEquals("George", dpk15Impl9.oldestFriends(person9List));
    }

}