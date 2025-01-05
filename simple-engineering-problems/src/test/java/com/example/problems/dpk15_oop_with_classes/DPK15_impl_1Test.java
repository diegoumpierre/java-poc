package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK15_impl_1Test {


    @Test
    void personAddFriendsShouldSuccess(){
        Person1 person1 = new Person1("John", 30);
        person1.addFriends("Paul");
        person1.addFriends("George");
        person1.addFriends("Ringo");
        List<String> expectedFriends = List.of("Paul","George", "Ringo");

        assertEquals("John", person1.getName());
        assertEquals(30, person1.getAge());
        assertArrayEquals(expectedFriends.toArray(), person1.getFriends().toArray());
    }


    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess(){
        Person1 person1 = new Person1("John", 30);
        person1.addFriends("Paul");
        person1.addFriends("Paul");

        assertEquals(1, person1.getFriends().size());
        assertEquals("Paul", person1.getFriends().get(0));
    }

    @Test
    void personRemoveFriendsShouldSuccess(){
        Person1 person1 = new Person1("John", 30);
        person1.addFriends("Paul");
        person1.addFriends("George");
        person1.addFriends("Ringo");
        List<String> expectedFriends = List.of("Paul", "Ringo");

        person1.removeFriend("George");
        assertArrayEquals(expectedFriends.toArray(), person1.getFriends().toArray());
    }

    @Test
    void moreFriendsShouldBeSuccess() {

        List<Person1> person1List = new ArrayList<>();

        Person1 person1 = new Person1("John", 30);
        person1.addFriends("Paul");
        person1.addFriends("George");
        person1List.add(person1);

        person1 = new Person1("Ringo", 89);
        person1.addFriends("Paul");
        person1.addFriends("George");
        person1.addFriends("Ringo");
        person1.addFriends("Ringo1");
        person1.addFriends("Ringo2");
        person1.addFriends("Ringo3");
        person1List.add(person1);

        person1 = new Person1("John1", 67);
        person1.addFriends("Paul");
        person1List.add(person1);


        DPK15_impl_1 dpk15Impl1 = new DPK15_impl_1();
        assertEquals("Ringo", dpk15Impl1.moreFriends(person1List));

    }

    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person1> person1List = new ArrayList<>();

        Person1 person1 = new Person1("John", 30);
        person1.addFriends("Paul");
        person1.addFriends("George");
        person1List.add(person1);

        person1 = new Person1("Ringo", 89);
        person1.addFriends("Paul");
        person1.addFriends("George");
        person1.addFriends("Ringo");
        person1.addFriends("Ringo1");
        person1.addFriends("Ringo2");
        person1.addFriends("Ringo3");
        person1List.add(person1);

        person1 = new Person1("John1", 67);
        person1.addFriends("Paul");
        person1List.add(person1);


        DPK15_impl_1 dpk15Impl1 = new DPK15_impl_1();
        assertEquals("John1", dpk15Impl1.lessFriends(person1List));

    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person1> person1List = new ArrayList<>();

        Person1 person1 = new Person1("John", 30);
        person1.addFriends("Paul",60);
        person1.addFriends("George",61);
        person1List.add(person1);

        person1 = new Person1("Ringo", 89);
        person1.addFriends("Paul",69);
        person1.addFriends("George", 70);
        person1.addFriends("Ringo",10);
        person1.addFriends("Ringo1",62);
        person1.addFriends("Ringo2",64);
        person1.addFriends("Ringo3",62);
        person1List.add(person1);

        person1 = new Person1("John1", 67);
        person1.addFriends("Paul",20);
        person1List.add(person1);


        DPK15_impl_1 dpk15Impl1 = new DPK15_impl_1();
        assertEquals("George", dpk15Impl1.oldestFriends(person1List));
    }

}