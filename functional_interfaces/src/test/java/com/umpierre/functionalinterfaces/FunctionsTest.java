package com.umpierre.functionalinterfaces;

import com.umpierre.functionalinterfaces.dataTest.DataForTest;

import com.umpierre.functionalinterfaces.domain.Person;
import com.umpierre.functionalinterfaces.util.ListToMap;
import org.junit.jupiter.api.Test;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {

    @Test
    public void functionTest(){
        Function<String,Integer> funcLength = x -> x.length();

        assertEquals(5,funcLength.apply("Diego"));
        assertEquals(3,funcLength.apply("POC"));
    }

    @Test
    public void functionTestChain(){
        Function<String,Integer> funcLength = x -> x.length();
        Function<Integer,Integer> funcMultiply2 = x -> x*2;

        assertEquals(10,funcLength.andThen(funcMultiply2).apply("Diego"));

    }


    @Test
    public void functionTestListToMap(){
        ListToMap resultLstToMap = new ListToMap();

        //Organize the map by Country
        Map<String, List<Person>> map = resultLstToMap.convertListToMap(DataForTest.gimmeListPerson(),
                resultLstToMap::getCountry);

        assertEquals(1,map.get("ITALY").size());
        assertEquals(2,map.get("PARIS").size());
        assertEquals(2,map.get("BRAZIL").size());
        assertEquals(1,map.get("SPAIN").size());

        //Organize the map by CompanyId
        map = resultLstToMap.convertListToMap(DataForTest.gimmeListPerson(), resultLstToMap::getCompanyId);

        assertEquals(3,map.get("30").size());
        assertEquals(2,map.get("32").size());
        assertEquals(1,map.get("45").size());
    }

    @Test
    public void uncodeUrl() throws UnsupportedEncodingException {

        String url = "M%26G";
            url = "Allen+%26+Company+LLC";

        String result = java.net.URLDecoder.decode(url, String.valueOf(StandardCharsets.UTF_8));


        System.out.println("Resultado final: "+result);



    }

}
