package com.example.problems.dpk15_oop_with_classes;

import net.bytebuddy.agent.builder.AgentBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DPK15_impl_1Test {

    @Test
    void personAddFriendsShpuldSuccess(){
        Person person = new Person("John", 30);
        person.addFriends("Paul");
        person.addFriends("George");
        person.addFriends("Ringo");
        Set<String> expectedFriends = Set.of("Paul","George", "Ringo");

        assertEquals(30, person.getAge());
        assertArrayEquals(expectedFriends.toArray(), person.getFriends().toArray());
        assertEquals("John", person.getName());

        //test de remove
        expectedFriends.remove("George");
        person.removeFriend("George");
        assertArrayEquals(expectedFriends.toArray(), person.getFriends().toArray());
    }

    /*
    In one of your classes, could you create a method that tell who is the person with more friends?
In one of your classes, could you create a method that tell who is the person with less friends?
In one of your classes, could you create a method that tell who is the person with the oldest friend?
     */

}