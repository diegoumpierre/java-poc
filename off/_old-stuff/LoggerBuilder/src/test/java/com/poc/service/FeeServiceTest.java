package com.poc.service;

import com.poc.domain.Category;
import com.poc.domain.Fee;
import com.poc.domain.StateEnum;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
class FeeServiceTest {

    @Spy
    FeeService feeService = new FeeService();

    @Before
    public void setUp() {






        initMocks(this);
    }

    @Test
    void insertShouldBeSuccess() {


        List<Category> list = DataTest.gimmeCategoryList();

        list.size();



//        Fee result = feeService.insert(StateEnum.SP.name(), 2020, 909.0d);
//
//        assertEquals(StateEnum.SP, result.getStateEnum());
//        assertEquals(2020, result.getYear());
//        assertEquals(909.0d, result.getValue());


    }

    @Test
    void removeShouldBeSuccess() {
        Fee feeToRemove = Fee.builder()
             //   .value(20d)
             //   .year(2050)
                .stateEnum(StateEnum.RJ)
                .build();
    }
  
    @Test
    void getFeeByStateAndYear() {

        Calendar cal  = Calendar.getInstance();
        cal.set(2014, 0, 25);
        Date dueDate = cal.getTime();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(2014, 0, 25, 0, 0, 0);

        Date dueDate2 =  calendar.getTime();

        LocalDate date = dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = dueDate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

//        System.out.println(date.isBefore(date2));


//        System.out.println(dueDate.toInstant());


        GregorianCalendar cal2 = new GregorianCalendar();
        cal2.setTime(dueDate2);
        System.out.println(cal2.getTime());


        Calendar calendar1  = Calendar.getInstance();
        calendar1.setTime( cal2.getTime() );
        System.out.println(calendar1.getTime());




    }
}