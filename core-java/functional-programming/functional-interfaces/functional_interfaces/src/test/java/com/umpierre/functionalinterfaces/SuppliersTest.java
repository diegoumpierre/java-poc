package com.umpierre.functionalinterfaces;

import com.umpierre.functionalinterfaces.util.CalendarUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.function.Supplier;

public class SuppliersTest {


    public class Calculate {
        public Supplier<Double> doSomeCalc() {
            Supplier<Double> supplierNow = () -> {
                double d = 10.64;
                double d1 = 1000.0;
                return d * d1;
            };
           return supplierNow;
        }

        public Supplier<Double> doSomeCalcOther() {
            Supplier<Double> supplierNow = () -> {
                double d = 10.64;
                double d1 = 10.0;
                return d * d1;
            };
            return supplierNow;
        }
    }

    private Double calcMeMultiplyTwo(Supplier<Double> valueToCalc) {
        Double result = valueToCalc.get();
        result = result*2;
        return result;
    }

    @Test
    public void tst(){
        Calculate c = new Calculate();
        System.out.println(c.doSomeCalc().get());//10640.0
        System.out.println(c.doSomeCalcOther().get());//106.4

        Double result1 = calcMeMultiplyTwo(c.doSomeCalc());
        Double result2 = calcMeMultiplyTwo(c.doSomeCalcOther());

        System.out.println(result1);//21280.0
        System.out.println(result2);//212.8
    }





    @Test
    public void supplierExample(){

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SSS");

        Supplier<LocalDateTime> supplierDateNow = () -> LocalDateTime.now();

        LocalDateTime time = supplierDateNow.get();



        System.out.println(time);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        time = supplierDateNow.get();
        System.out.println("Time after sleep: "+time);


        Supplier<String> s1 = () -> dtf.format(LocalDateTime.now());
        String time2 = s1.get();
        System.out.println("Time2: "+time2);

        Supplier<String> s2 = () -> dtf.format(supplierDateNow.get());
        String time3 = s2.get();
        System.out.println("Time3: "+time3);


        CalendarUtil calendarUtil = new CalendarUtil();
        System.out.println(dtf.format(calendarUtil.supplierDefinedOtherClassExample().get()));
    }


}
