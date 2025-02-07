package com.poc.taxsystem.service;

public enum TaxYearStateStrategy {

    SC_2023(new SC2023Tax()),
    SC_2024(new SC2024Tax()),
    SC_2025(new SC2025Tax()),
    RS_2023(new RS2023Tax()),
    RS_2024(new RS2024Tax()),
    RS_2025(new RS2025Tax());

    private final TaxApplication taxApplication;

    TaxYearStateStrategy(TaxApplication taxApplication) {
        this.taxApplication = taxApplication;
    }

    public TaxApplication getTaxApplication() {
        return taxApplication;
    }


}
