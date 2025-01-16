package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK15_impl_10Test {


    @Test
    void personCreateShouldBeSuccess() {
        Person10 person10 = new Person10("John", 30);
        person10.addFriend("Paul");
        person10.addFriend("George");
        person10.addFriend("Ringo");
        assertEquals(3, person10.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess() {
        Person10 person10 = new Person10("John", 30);
        assertEquals("John", person10.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess() {
        Person10 person10 = new Person10("John", 30);
        person10.addFriend("Paul");
        person10.addFriend("George");
        person10.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }


    @Test
    void returnAgeShouldVeSuccess() {
        Person10 person10 = new Person10("John", 30);
        assertEquals(30, person10.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess() {
        Person10 person10 = new Person10("John", 30);
        person10.addFriend("Paul");
        person10.addFriend("Paul");

        assertEquals(1, person10.getFriends().size());
        assertEquals("Paul", person10.getFriends().get(0).getName());
    }


    @Test
    void personRemoveFriendsShouldSuccess() {
        Person10 person10 = new Person10("John", 30);
        person10.addFriend("Paul");
        person10.addFriend("George");
        person10.addFriend("Ringo");

        List<Friend10> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend10("Paul"));
        expectedFriends.add(new Friend10("Ringo"));

        person10.removeFriend("George");
        assertEquals(2, person10.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person10.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person10> person10List = new ArrayList<>();

        Person10 person10 = new Person10("John", 30);
        person10.addFriend("Paul");
        person10.addFriend("George");
        person10List.add(person10);

        person10 = new Person10("Ringo", 89);
        person10.addFriend("Paul");
        person10.addFriend("George");
        person10.addFriend("Ringo");
        person10.addFriend("Ringo1");
        person10.addFriend("Ringo2");
        person10.addFriend("Ringo3");
        person10List.add(person10);

        person10 = new Person10("John1", 67);
        person10.addFriend("Paul");
        person10List.add(person10);


        DPK15_impl_10 dpk15Impl10 = new DPK15_impl_10();
        assertEquals("Ringo", dpk15Impl10.moreFriends(person10List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person10> person10List = new ArrayList<>();

        Person10 person10 = new Person10("John", 30);
        person10.addFriend("Paul");
        person10.addFriend("George");
        person10List.add(person10);

        person10 = new Person10("Ringo", 89);
        person10.addFriend("Paul");
        person10.addFriend("George");
        person10.addFriend("Ringo2");
        person10.addFriend("Ringo");
        person10.addFriend("Ringo1");
        person10.addFriend("Ringo3");
        person10List.add(person10);

        person10 = new Person10("John1", 67);
        person10.addFriend("Paul");
        person10List.add(person10);


        DPK15_impl_10 dpk15Impl10 = new DPK15_impl_10();
        assertEquals("John1", dpk15Impl10.lessFriends(person10List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person10> person10List = new ArrayList<>();

        Person10 person10 = new Person10("John", 30);
        person10.addFriend("Paul", 60);
        person10.addFriend("George", 61);
        person10List.add(person10);

        person10 = new Person10("Ringo", 89);
        person10.addFriend("Paul", 69);
        person10.addFriend("George", 70);
        person10.addFriend("Ringo", 10);
        person10.addFriend("Ringo1", 62);
        person10.addFriend("Ringo2", 64);
        person10.addFriend("Ringo3", 62);
        person10List.add(person10);

        person10 = new Person10("John1", 67);
        person10.addFriend("Paul", 20);
        person10List.add(person10);


        DPK15_impl_10 dpk15Impl10 = new DPK15_impl_10();
        assertEquals("George", dpk15Impl10.oldestFriends(person10List));
    }


}