package com.example.problems.dpk15_oop_with_classes;

import net.bytebuddy.agent.builder.AgentBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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


    /*
    In one of your classes, could you create a method that tell who is the person with more friends?
In one of your classes, could you create a method that tell who is the person with less friends?
In one of your classes, could you create a method that tell who is the person with the oldest friend?
     */

}