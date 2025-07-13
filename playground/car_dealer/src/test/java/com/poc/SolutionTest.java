package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 16 2023
 */
class SolutionTest {
    private Solution basicStructure;

    private List<Car> carStock =new ArrayList<>();


    @BeforeEach
    void init(){
        basicStructure = new Solution();
    }


    @Test
    void getListCarShouldSuccess(){
        loadCars();
        List<Car> expected = new ArrayList<>();
        expected.add(Car.builder().maker("Toyota").model("Corolla").year(2017).price(12000.00).miles(60000).build());
        expected.add(Car.builder().maker("Toyota").model("Camry").year(2018).price(15000.00).miles(50000).build());

        List<Car> result = basicStructure.findCars(carStock,20000,70000);

        assertArrayEquals(expected.toArray(), result.toArray());

    }


    private void loadCars(){
        carStock.add(Car.builder().maker("Toyota").model("Camry").year(2018).price(15000.00).miles(50000).build());
        carStock.add(Car.builder().maker("Ford").model("F-150").year(2020).price(25000.00).miles(80000).build());
        carStock.add(Car.builder().maker("Honda").model("Accord").year(2015).price(10000.00).miles(120000).build());
        carStock.add(Car.builder().maker("Toyota").model("Corolla").year(2017).price(12000.00).miles(60000).build());
        carStock.add(Car.builder().maker("Nissan").model("Altima").year(2019).price(18000.00).miles(90000).build());
        carStock.add(Car.builder().maker("Chevrolet").model("Camaro").year(2021).price(30000.00).miles(20000).build());
    }
}