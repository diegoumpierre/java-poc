package com.poc.specificationpattern;

import com.poc.specificationpattern.TaxSystemService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TaxSystemServiceTest {


    @Test
    void calculateTaxShouldBeSuccess() {

        TaxSpecification stateTaxSpecification = new StateTaxSpecification("RS",2023);
//        TaxSpecification yearTaxSpecification = new YearTaxSpecification(2024);

        List<TaxSpecification> specifications = Arrays.asList(stateTaxSpecification);

        TaxSystemService taxSystemService = new TaxSystemService(specifications);

        double tax = taxSystemService.calculateTax("RS", 2024, 100.00);
        assertEquals(100.0, tax);
    }

    @Test
    void calculateTaxWrongStateShouldBeFail() {

        TaxSpecification stateTaxSpecification = new StateTaxSpecification("RS");
//        TaxSpecification yearTaxSpecification = new YearTaxSpecification(2024);

        List<TaxSpecification> specifications = Arrays.asList(stateTaxSpecification, yearTaxSpecification);

        TaxSystemService taxSystemService = new TaxSystemService(specifications);

        double tax = taxSystemService.calculateTax("SC", 2024, 100.00);
        assertEquals(50.0, tax);
    }

    @Test
    void calculateTaxWrongYearShouldBeFail() {

        TaxSpecification stateTaxSpecification = new StateTaxSpecification("RS");
        TaxSpecification yearTaxSpecification = new YearTaxSpecification(2024);

        List<TaxSpecification> specifications = Arrays.asList(stateTaxSpecification, yearTaxSpecification);

        TaxSystemService taxSystemService = new TaxSystemService(specifications);

        double tax = taxSystemService.calculateTax("RS", 2025, 100.00);
        assertEquals(50.0, tax);
    }
}