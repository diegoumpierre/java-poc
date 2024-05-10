package com.poc.client;

import com.poc.client.client1.domain.Person;
import com.poc.client.client1.service.PersonServiceHelper;
import com.poc.client.client2.domain.Car;
import com.poc.client.client2.service.CarServiceHelper;

public class Main {


    public static void main(String[] args) throws Exception {

        //client1
        Person person = new Person("Diego", 30, "81401310087");
        PersonServiceHelper personServiceHelper = new PersonServiceHelper();
        personServiceHelper.doSomeProcess(person);

        //client2
        Car car = new Car("Tubarao", 10);
        CarServiceHelper carServiceHelper = new CarServiceHelper();
        carServiceHelper.howManyProcess(car);

    }



}
