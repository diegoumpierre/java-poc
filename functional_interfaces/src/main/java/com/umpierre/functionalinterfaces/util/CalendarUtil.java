package com.umpierre.functionalinterfaces.util;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class CalendarUtil{

    public Supplier<LocalDateTime> supplierDefinedOtherClassExample() {

        //Make a lot of steps here

        // constructor reference
        return () -> LocalDateTime.now();

    }


}
