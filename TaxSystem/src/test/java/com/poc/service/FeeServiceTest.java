package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.StateEnum;
import com.poc.domain.TaxSystem;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class FeeServiceTest {

    FeeService feeService = new FeeService();

    @Test
    void insertShouldBeSuccess() {
        Fee result = feeService.insert(StateEnum.SP,2020,909.0d);

        assertEquals(StateEnum.SP, result.getStateEnum());
        assertEquals(2020, result.getYear());
        assertEquals(909.0d, result.getValue());

        assertThat(TaxSystem.FEE_LIST, hasItem(result));
    }

    @Test
    void removeShouldBeSuccess() {
        Fee feeToRemove = Fee.builder()
                .value(20d)
                .year(2050)
                .stateEnum(StateEnum.RJ)
                .build();
        TaxSystem.FEE_LIST.addAll(DataTest.gimmeFeeList(20));
        TaxSystem.FEE_LIST.add(feeToRemove);
        assertThat(TaxSystem.FEE_LIST, hasItem(feeToRemove));
        feeService.remove(StateEnum.RJ,2050);
        assertThat(TaxSystem.FEE_LIST, not(hasItem(feeToRemove)));
    }

    @Test
    void getFeeByStateAndYear() {
    }

    @Test
    void removeAllTaxesBefore() {
    }

    @Test
    void insertTaxForTheYear() {
    }
}