package com.umpierre.functionalinterfaces;

import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BiFuncionalTest {

    @Test
    public void biFunctionalExample(){
        BiFunction<Integer, Integer, Integer> functionMultiply = (firstValue, secondValue) -> firstValue * secondValue;
        assertEquals(6,functionMultiply.apply(2,3));
    }

    @Test
    public void biFunctionalExamplePlusFunction() {

        BiFunction<Integer, Integer, Double> functionPow = (firstValue, elevatedOn) -> Math.pow(firstValue, elevatedOn);

        // takes Double, returns Integer
        Function<Double, Integer> functionMultiplyBytoo = (input) -> input.intValue() * 2;

        assertEquals(8, functionPow.andThen(functionMultiplyBytoo).apply(2, 2));
    }

    @Test
    public void biFunctionalExamplePassFunctionByParameter() {
        Integer result = recivedBiFunctionAndFunctionByPameter(2, 2,
                (firstParamValue, secondParamValue) -> Math.pow(firstParamValue, secondParamValue),
                (resultFromFirstFunction) -> resultFromFirstFunction.intValue() * 2);
        assertEquals(8, result);


        result = recivedBiFunctionAndFunctionByPameter(2, 2,
                (firstParamValue, secondParamValue) -> new Double(firstParamValue - secondParamValue),
                (resultFromFirstFunction) -> resultFromFirstFunction.intValue() + 2);
        assertEquals(2, result);


    }

    public static Integer recivedBiFunctionAndFunctionByPameter(Integer firstParamValue, Integer secondParamValue,
                                      BiFunction<Integer, Integer, Double> biFunctionParameter,
                                      Function<Double, Integer> functionParameter) {

        return biFunctionParameter.andThen(functionParameter).apply(firstParamValue, secondParamValue);

    }

}
