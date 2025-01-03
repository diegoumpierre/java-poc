package com.example.problems.dpk15_oop_with_classes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK15_impl_1Test {


    @Test
    void personAddFriendsShouldSuccess(){
        Person person = new Person("John", 30);
        person.addFriends("Paul");
        person.addFriends("George");
        person.addFriends("Ringo");
        List<String> expectedFriends = List.of("Paul","George", "Ringo");

        assertEquals("John", person.getName());
        assertEquals(30, person.getAge());
        assertArrayEquals(expectedFriends.toArray(), person.getFriends().toArray());
    }


    @Test
    void dontAllowTheSameFriendTwiceShouldBeSuccess(){
        Person person = new Person("John", 30);
        person.addFriends("Paul");
        person.addFriends("Paul");

        assertEquals(1,person.getFriends().size());
        assertEquals("Paul",person.getFriends().get(0));
    }

    @Test
    void personRemoveFriendsShouldSuccess(){
        Person person = new Person("John", 30);
        person.addFriends("Paul");
        person.addFriends("George");
        person.addFriends("Ringo");
        List<String> expectedFriends = List.of("Paul", "Ringo");

        person.removeFriend("George");
        assertArrayEquals(expectedFriends.toArray(), person.getFriends().toArray());
    }

    @Test
    void moreFriendsShouldBeSuccess() {

        List<Person> personList = new ArrayList<>();

        Person person = new Person("John", 30);
        person.addFriends("Paul");
        person.addFriends("George");
        personList.add(person);

        person = new Person("Ringo", 89);
        person.addFriends("Paul");
        person.addFriends("George");
        person.addFriends("Ringo");
        person.addFriends("Ringo1");
        person.addFriends("Ringo2");
        person.addFriends("Ringo3");
        personList.add(person);

        person = new Person("John1", 67);
        person.addFriends("Paul");
        personList.add(person);


        DPK15_impl_1 dpk15Impl1 = new DPK15_impl_1();
        assertEquals("Ringo", dpk15Impl1.moreFriends(personList));

    }

    @Test
    void lessFriendsShouldBeSuccess() {

        List<Person> personList = new ArrayList<>();

        Person person = new Person("John", 30);
        person.addFriends("Paul");
        person.addFriends("George");
        personList.add(person);

        person = new Person("Ringo", 89);
        person.addFriends("Paul");
        person.addFriends("George");
        person.addFriends("Ringo");
        person.addFriends("Ringo1");
        person.addFriends("Ringo2");
        person.addFriends("Ringo3");
        personList.add(person);

        person = new Person("John1", 67);
        person.addFriends("Paul");
        personList.add(person);


        DPK15_impl_1 dpk15Impl1 = new DPK15_impl_1();
        assertEquals("John1", dpk15Impl1.lessFriends(personList));

    }

    @Test
    void oldestFriendsShouldBeSuccess() {

        List<Person> personList = new ArrayList<>();

        Person person = new Person("John", 30);
        person.addFriends("Paul",60);
        person.addFriends("George",61);
        personList.add(person);

        person = new Person("Ringo", 89);
        person.addFriends("Paul",69);
        person.addFriends("George", 70);
        person.addFriends("Ringo",10);
        person.addFriends("Ringo1",62);
        person.addFriends("Ringo2",64);
        person.addFriends("Ringo3",62);
        personList.add(person);

        person = new Person("John1", 67);
        person.addFriends("Paul",20);
        personList.add(person);


        DPK15_impl_1 dpk15Impl1 = new DPK15_impl_1();
        assertEquals("George", dpk15Impl1.oldestFriends(personList));
    }

}