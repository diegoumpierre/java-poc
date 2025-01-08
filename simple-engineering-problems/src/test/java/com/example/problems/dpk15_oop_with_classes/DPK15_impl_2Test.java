package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK15_impl_2Test {

    @Test
    void personCreationShouldBeSuccess(){
        Person2 person2 = new Person2("John", 30);
        person2.addFriend("Paul");
        person2.addFriend("George");
        person2.addFriend("Ringo");
        assertEquals(3, person2.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess(){
        Person2 person2 = new Person2("John", 30);
        assertEquals("John", person2.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess(){
        Person2 person2 = new Person2("John", 30);
        person2.addFriend("Paul");
        person2.addFriend("George");
        person2.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess(){
        Person2 person2 = new Person2("John", 30);
        assertEquals(30, person2.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess(){
        Person2 person2 = new Person2("John", 30);
        person2.addFriend("Paul");
        person2.addFriend("Paul");

        assertEquals(1, person2.getFriends().size());
        assertEquals("Paul", person2.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess(){
        Person2 person2 = new Person2("John", 30);
        person2.addFriend("Paul");
        person2.addFriend("George");
        person2.addFriend("Ringo");

        List<Friend2> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend2("Paul"));
        expectedFriends.add(new Friend2("Ringo"));

        person2.removeFriend("George");
        assertEquals(2, person2.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person2.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person2> person2List = new ArrayList<>();

        Person2 person2 = new Person2("John", 30);
        person2.addFriend("Paul");
        person2.addFriend("George");
        person2List.add(person2);

        person2 = new Person2("Ringo", 89);
        person2.addFriend("Paul");
        person2.addFriend("George");
        person2.addFriend("Ringo");
        person2.addFriend("Ringo1");
        person2.addFriend("Ringo2");
        person2.addFriend("Ringo3");
        person2List.add(person2);

        person2 = new Person2("John1", 67);
        person2.addFriend("Paul");
        person2List.add(person2);


        DPK15_impl_2 dpk15Impl2 = new DPK15_impl_2();
        assertEquals("Ringo", dpk15Impl2.moreFriends(person2List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person2> person2List = new ArrayList<>();

        Person2 person2 = new Person2("John", 30);
        person2.addFriend("Paul");
        person2.addFriend("George");
        person2List.add(person2);

        person2 = new Person2("Ringo", 89);
        person2.addFriend("Paul");
        person2.addFriend("George");
        person2.addFriend("Ringo2");
        person2.addFriend("Ringo");
        person2.addFriend("Ringo1");
        person2.addFriend("Ringo3");
        person2List.add(person2);

        person2 = new Person2("John1", 67);
        person2.addFriend("Paul");
        person2List.add(person2);


        DPK15_impl_2 dpk15Impl2 = new DPK15_impl_2();
        assertEquals("John1", dpk15Impl2.lessFriends(person2List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person2> person2List = new ArrayList<>();

        Person2 person2 = new Person2("John", 30);
        person2.addFriend("Paul",60);
        person2.addFriend("George",61);
        person2List.add(person2);

        person2 = new Person2("Ringo", 89);
        person2.addFriend("Paul",69);
        person2.addFriend("George", 70);
        person2.addFriend("Ringo",10);
        person2.addFriend("Ringo1",62);
        person2.addFriend("Ringo2",64);
        person2.addFriend("Ringo3",62);
        person2List.add(person2);

        person2 = new Person2("John1", 67);
        person2.addFriend("Paul",20);
        person2List.add(person2);


        DPK15_impl_2 dpk15Impl2 = new DPK15_impl_2();
        assertEquals("George", dpk15Impl2.oldestFriends(person2List));
    }

}