package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK15_impl_7Test {


    @Test
    void personCreationShouldBeSuccess() {
        Person7 person7 = new Person7("John", 30);
        person7.addFriend("Paul");
        person7.addFriend("George");
        person7.addFriend("Ringo");
        assertEquals(3, person7.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess() {
        Person7 person7 = new Person7("John", 30);
        assertEquals("John", person7.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess() {
        Person7 person7 = new Person7("John", 30);
        person7.addFriend("Paul");
        person7.addFriend("George");
        person7.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess() {
        Person7 person7 = new Person7("John", 30);
        assertEquals(30, person7.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess() {
        Person7 person7 = new Person7("John", 30);
        person7.addFriend("Paul");
        person7.addFriend("Paul");

        assertEquals(1, person7.getFriends().size());
        assertEquals("Paul", person7.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess() {
        Person7 person7 = new Person7("John", 30);
        person7.addFriend("Paul");
        person7.addFriend("George");
        person7.addFriend("Ringo");

        List<Friend7> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend7("Paul"));
        expectedFriends.add(new Friend7("Ringo"));

        person7.removeFriend("George");
        assertEquals(2, person7.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person7.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person7> person7List = new ArrayList<>();

        Person7 person7 = new Person7("John", 30);
        person7.addFriend("Paul");
        person7.addFriend("George");
        person7List.add(person7);

        person7 = new Person7("Ringo", 89);
        person7.addFriend("Paul");
        person7.addFriend("George");
        person7.addFriend("Ringo");
        person7.addFriend("Ringo1");
        person7.addFriend("Ringo2");
        person7.addFriend("Ringo3");
        person7List.add(person7);

        person7 = new Person7("John1", 67);
        person7.addFriend("Paul");
        person7List.add(person7);


        DPK15_impl_7 dpk15Impl7 = new DPK15_impl_7();
        assertEquals("Ringo", dpk15Impl7.moreFriends(person7List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person7> person7List = new ArrayList<>();

        Person7 person7 = new Person7("John", 30);
        person7.addFriend("Paul");
        person7.addFriend("George");
        person7List.add(person7);

        person7 = new Person7("Ringo", 89);
        person7.addFriend("Paul");
        person7.addFriend("George");
        person7.addFriend("Ringo2");
        person7.addFriend("Ringo");
        person7.addFriend("Ringo1");
        person7.addFriend("Ringo3");
        person7List.add(person7);

        person7 = new Person7("John1", 67);
        person7.addFriend("Paul");
        person7List.add(person7);


        DPK15_impl_7 dpk15Impl7 = new DPK15_impl_7();
        assertEquals("John1", dpk15Impl7.lessFriends(person7List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person7> person7List = new ArrayList<>();

        Person7 person7 = new Person7("John", 30);
        person7.addFriend("Paul", 60);
        person7.addFriend("George", 61);
        person7List.add(person7);

        person7 = new Person7("Ringo", 89);
        person7.addFriend("Paul", 69);
        person7.addFriend("George", 70);
        person7.addFriend("Ringo", 10);
        person7.addFriend("Ringo1", 62);
        person7.addFriend("Ringo2", 64);
        person7.addFriend("Ringo3", 62);
        person7List.add(person7);

        person7 = new Person7("John1", 67);
        person7.addFriend("Paul", 20);
        person7List.add(person7);


        DPK15_impl_7 dpk15Impl7 = new DPK15_impl_7();
        assertEquals("George", dpk15Impl7.oldestFriends(person7List));
    }


}