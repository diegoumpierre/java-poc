package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK15_impl_8Test {
    @Test
    void personCreationShouldBeSuccess() {
        Person8 person8 = new Person8("John", 30);
        person8.addFriend("Paul");
        person8.addFriend("George");
        person8.addFriend("Ringo");
        assertEquals(3, person8.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess() {
        Person8 person8 = new Person8("John", 30);
        assertEquals("John", person8.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess() {
        Person8 person8 = new Person8("John", 30);
        person8.addFriend("Paul");
        person8.addFriend("George");
        person8.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess() {
        Person8 person8 = new Person8("John", 30);
        assertEquals(30, person8.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess() {
        Person8 person8 = new Person8("John", 30);
        person8.addFriend("Paul");
        person8.addFriend("Paul");

        assertEquals(1, person8.getFriends().size());
        assertEquals("Paul", person8.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess() {
        Person8 person8 = new Person8("John", 30);
        person8.addFriend("Paul");
        person8.addFriend("George");
        person8.addFriend("Ringo");

        List<Friend8> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend8("Paul"));
        expectedFriends.add(new Friend8("Ringo"));

        person8.removeFriend("George");
        assertEquals(2, person8.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person8.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person8> person8List = new ArrayList<>();

        Person8 person8 = new Person8("John", 30);
        person8.addFriend("Paul");
        person8.addFriend("George");
        person8List.add(person8);

        person8 = new Person8("Ringo", 89);
        person8.addFriend("Paul");
        person8.addFriend("George");
        person8.addFriend("Ringo");
        person8.addFriend("Ringo1");
        person8.addFriend("Ringo2");
        person8.addFriend("Ringo3");
        person8List.add(person8);

        person8 = new Person8("John1", 67);
        person8.addFriend("Paul");
        person8List.add(person8);


        DPK15_impl_8 dpk15Impl8 = new DPK15_impl_8();
        assertEquals("Ringo", dpk15Impl8.moreFriends(person8List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person8> person8List = new ArrayList<>();

        Person8 person8 = new Person8("John", 30);
        person8.addFriend("Paul");
        person8.addFriend("George");
        person8List.add(person8);

        person8 = new Person8("Ringo", 89);
        person8.addFriend("Paul");
        person8.addFriend("George");
        person8.addFriend("Ringo2");
        person8.addFriend("Ringo");
        person8.addFriend("Ringo1");
        person8.addFriend("Ringo3");
        person8List.add(person8);

        person8 = new Person8("John1", 67);
        person8.addFriend("Paul");
        person8List.add(person8);


        DPK15_impl_8 dpk15Impl8 = new DPK15_impl_8();
        assertEquals("John1", dpk15Impl8.lessFriends(person8List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person8> person8List = new ArrayList<>();

        Person8 person8 = new Person8("John", 30);
        person8.addFriend("Paul", 60);
        person8.addFriend("George1", 61);
        person8List.add(person8);

        person8 = new Person8("Ringo", 89);
        person8.addFriend("Paul", 69);
        person8.addFriend("George", 70);
        person8.addFriend("Ringo", 10);
        person8.addFriend("Ringo1", 62);
        person8.addFriend("Ringo2", 64);
        person8.addFriend("Ringo3", 62);
        person8List.add(person8);

        person8 = new Person8("John1", 67);
        person8.addFriend("Paul", 20);
        person8List.add(person8);


        DPK15_impl_8 dpk15Impl8 = new DPK15_impl_8();
        assertEquals("George", dpk15Impl8.oldestFriends(person8List));
    }
}