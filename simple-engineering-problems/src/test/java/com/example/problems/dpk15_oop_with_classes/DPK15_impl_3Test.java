package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK15_impl_3Test {

    @Test
    void personCreationShouldBeSuccess(){
        Person3 person3 = new Person3("John", 30);
        person3.addFriend("Paul");
        person3.addFriend("George");
        person3.addFriend("Ringo");
        assertEquals(3, person3.getFriends().size());
    }

    @Test
    void returnNameShouldBeSuccess(){
        Person3 person3 = new Person3("John", 30);
        assertEquals("John", person3.getName());
    }

    @Test
    void returnFriendsListShouldBeSuccess(){
        Person3 person3 = new Person3("John", 30);
        person3.addFriend("Paul");
        person3.addFriend("George");
        person3.addFriend("Ringo");

        List<String> expectedList = new ArrayList<>();
        expectedList.add("Paul");
        expectedList.add("George");
        expectedList.add("Ringo");

        assertArrayEquals(expectedList.toArray(), expectedList.toArray());

    }

    @Test
    void returnAgeShouldVeSuccess(){
        Person3 person3 = new Person3("John", 30);
        assertEquals(30, person3.getAge());
    }

    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess(){
        Person3 person3 = new Person3("John", 30);
        person3.addFriend("Paul");
        person3.addFriend("Paul");

        assertEquals(1, person3.getFriends().size());
        assertEquals("Paul", person3.getFriends().get(0).getName());
    }

    @Test
    void personRemoveFriendsShouldSuccess(){
        Person3 person3 = new Person3("John", 30);
        person3.addFriend("Paul");
        person3.addFriend("George");
        person3.addFriend("Ringo");

        List<Friend3> expectedFriends = new ArrayList<>();
        expectedFriends.add(new Friend3("Paul"));
        expectedFriends.add(new Friend3("Ringo"));

        person3.removeFriend("George");
        assertEquals(2, person3.getFriends().size());
        assertEquals(expectedFriends.get(0).getName(), person3.getFriends().get(0).getName());
    }


    @Test
    void moreFriendsShouldBeSuccess() {
        List<Person3> person3List = new ArrayList<>();

        Person3 person3 = new Person3("John", 30);
        person3.addFriend("Paul");
        person3.addFriend("George");
        person3List.add(person3);

        person3 = new Person3("Ringo", 89);
        person3.addFriend("Paul");
        person3.addFriend("George");
        person3.addFriend("Ringo");
        person3.addFriend("Ringo1");
        person3.addFriend("Ringo2");
        person3.addFriend("Ringo3");
        person3List.add(person3);

        person3 = new Person3("John1", 67);
        person3.addFriend("Paul");
        person3List.add(person3);


        DPK15_impl_3 dpk15Impl3 = new DPK15_impl_3();
        assertEquals("Ringo", dpk15Impl3.moreFriends(person3List));
    }


    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person3> person3List = new ArrayList<>();

        Person3 person3 = new Person3("John", 30);
        person3.addFriend("Paul");
        person3.addFriend("George");
        person3List.add(person3);

        person3 = new Person3("Ringo", 89);
        person3.addFriend("Paul");
        person3.addFriend("George");
        person3.addFriend("Ringo2");
        person3.addFriend("Ringo");
        person3.addFriend("Ringo1");
        person3.addFriend("Ringo3");
        person3List.add(person3);

        person3 = new Person3("John1", 67);
        person3.addFriend("Paul");
        person3List.add(person3);


        DPK15_impl_3 dpk15Impl3 = new DPK15_impl_3();
        assertEquals("John1", dpk15Impl3.lessFriends(person3List));
    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person3> person3List = new ArrayList<>();

        Person3 person3 = new Person3("John", 30);
        person3.addFriend("Paul",60);
        person3.addFriend("George",61);
        person3List.add(person3);

        person3 = new Person3("Ringo", 89);
        person3.addFriend("Paul",69);
        person3.addFriend("George", 70);
        person3.addFriend("Ringo",10);
        person3.addFriend("Ringo1",62);
        person3.addFriend("Ringo2",64);
        person3.addFriend("Ringo3",62);
        person3List.add(person3);

        person3 = new Person3("John1", 67);
        person3.addFriend("Paul",20);
        person3List.add(person3);


        DPK15_impl_3 dpk15Impl3 = new DPK15_impl_3();
        assertEquals("George", dpk15Impl3.oldestFriends(person3List));
    }

}