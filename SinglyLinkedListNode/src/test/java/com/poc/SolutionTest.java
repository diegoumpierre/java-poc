package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 19 2023
 */
class SolutionTest {
    private Solution solution;
    @BeforeEach
    void init(){
        solution = new Solution();
    }
    @Test
    void testCase1() {
        Solution.SinglyLinkedListNode node = new Solution.SinglyLinkedListNode(1);
        Solution.SinglyLinkedListNode node1 = new Solution.SinglyLinkedListNode(2);
        Solution.SinglyLinkedListNode node2 = new Solution.SinglyLinkedListNode(3);
        Solution.SinglyLinkedListNode node3 = new Solution.SinglyLinkedListNode(4);
        Solution.SinglyLinkedListNode node4 = new Solution.SinglyLinkedListNode(5);

        node.next = node1;
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;


        assertFalse(solution.hasCycle(node));
    }
    @Test
    void testCase2() {

        Solution.SinglyLinkedListNode node = new Solution.SinglyLinkedListNode(1);
        Solution.SinglyLinkedListNode node1 = new Solution.SinglyLinkedListNode(2);
        Solution.SinglyLinkedListNode node2 = new Solution.SinglyLinkedListNode(3);
        Solution.SinglyLinkedListNode node3 = new Solution.SinglyLinkedListNode(1);
        Solution.SinglyLinkedListNode node4 = new Solution.SinglyLinkedListNode(5);

        node.next = node1;
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;

        assertTrue(solution.hasCycle(node));
    }

    @Test
    void testCase3() {

        Solution.SinglyLinkedListNode node = new Solution.SinglyLinkedListNode(1);
        Solution.SinglyLinkedListNode node1 = new Solution.SinglyLinkedListNode(2);
        Solution.SinglyLinkedListNode node2 = new Solution.SinglyLinkedListNode(3);
        Solution.SinglyLinkedListNode node3 = new Solution.SinglyLinkedListNode(-1);
        Solution.SinglyLinkedListNode node4 = new Solution.SinglyLinkedListNode(1);

        node.next = node1;
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;

        assertTrue(solution.hasCycle(node));
    }

    @Test
    void testCase4() {

        Solution.SinglyLinkedListNode node = new Solution.SinglyLinkedListNode(5);
        Solution.SinglyLinkedListNode node1 = new Solution.SinglyLinkedListNode(-1);
        Solution.SinglyLinkedListNode node2 = new Solution.SinglyLinkedListNode(21);
        Solution.SinglyLinkedListNode node3 = new Solution.SinglyLinkedListNode(5);
        Solution.SinglyLinkedListNode node4 = null;

        node.next = node1;
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;

        assertTrue(solution.hasCycle(node));
    }



}