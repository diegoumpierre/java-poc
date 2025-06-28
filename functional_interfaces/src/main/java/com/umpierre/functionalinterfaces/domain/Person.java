package com.umpierre.functionalinterfaces.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {

    private String companyId;
    private String name;
    private String country;


}
