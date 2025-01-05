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

}