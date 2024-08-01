package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.StateEnum;
import com.poc.domain.TaxSystem;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class FeeServiceTest {

    FeeService feeService = new FeeService();

    @Test
    void insert() {

        Fee result = feeService.insert(StateEnum.RS,2020,909.0d);

        assertEquals(StateEnum.RS, result.getStateEnum());
        assertEquals(2020, result.getYear());
        assertEquals(909.0d, result.getValue());

        assertEquals(1, TaxSystem.FEE_LIST.size());
    }


    @Test
    void testInsert() {
    }

}