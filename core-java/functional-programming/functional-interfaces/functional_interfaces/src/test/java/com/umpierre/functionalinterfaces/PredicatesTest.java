package com.umpierre.functionalinterfaces;

import com.umpierre.functionalinterfaces.domain.Person;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.umpierre.functionalinterfaces.dataTest.DataForTest.gimmeListNames;
import static com.umpierre.functionalinterfaces.dataTest.DataForTest.gimmeListOneToTen;
import static com.umpierre.functionalinterfaces.dataTest.DataForTest.gimmeListPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredicatesTest {


    @Test
    public void predicateFilter(){
        Predicate<Integer> greaterThanFive = x-> x > 5 ;

        List<Integer> collect = gimmeListOneToTen().stream()
                .filter(greaterThanFive)
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(6, 7, 8, 9, 10),collect);
    }

    @Test
    public void predicateAndTwoFilters(){
        Predicate<Integer> greaterThanOne = x -> x > 1;
        Predicate<Integer> lessThanFive = x -> x < 5;

        List<Integer> collect = gimmeListOneToTen().stream()
                .filter(greaterThanOne)
                .filter(lessThanFive)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(2, 3, 4),collect);
    }

    @Test
    public void predicateAnd(){
        Predicate<Integer> greaterThanOne = x -> x > 1;
        Predicate<Integer> lessThanFive = x -> x < 5;

        List<Integer> collect = gimmeListOneToTen().stream()
                .filter(greaterThanOne.and(lessThanFive))
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(2, 3, 4),collect);
    }


    @Test
    public void predicateOR(){
        Predicate<String> initialD = x -> x.startsWith("D");
        Predicate<String> lenght4 = x -> x.length() == 4;

        List<String> collect = gimmeListNames().stream()
                .filter(initialD.or(lenght4))
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("Diego", "Daniel", "Mark","Dani"),collect);
        assertEquals(8,gimmeListNames().size());
    }

    @Test
    public void predicateNegate(){
        Predicate<String> initialD = x -> x.startsWith("D");

        List<String> collect = gimmeListNames().stream()
                .filter(initialD.negate())
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("Tiogo", "Marcelo", "Marco","Antonio","Mark"),collect);
        assertEquals(8,gimmeListNames().size());
    }


    @Test
    public void predicateChaining(){
        Predicate<String> initialD = x -> x.startsWith("D") ||gimmeListNames().equals("") || x.toUpperCase().equals("e");
        Predicate<String> lenght4 = x -> x.length() == 4;

        List<String> collect = gimmeListNames().stream()
                .filter(initialD.negate().or(lenght4))
                .collect(Collectors.toList());






        assertEquals(Arrays.asList("Tiogo", "Marcelo", "Marco","Antonio","Mark","Dani"),collect);
        assertEquals(8,gimmeListNames().size());
    }


    @Test
    public void predicateChaining1(){

        List<String> test = Arrays.asList("502,503,504");


        Predicate<String> initialD = x -> test.contains(x);
        Predicate<String> lenght4 = x -> x.length() == 4;

        List<String> collect = gimmeListNames().stream()
                .filter(initialD)
                .collect(Collectors.toList());


        assertEquals(Arrays.asList("Tiogo", "Marcelo", "Marco","Antonio","Mark","Dani"),collect);
        assertEquals(8,gimmeListNames().size());
    }


    @Test
    public void predicateInObject(){
        List<Person> result = gimmeListPerson().stream()
                .filter(x -> x.getCountry().equals("BRAZIL"))
                .collect(Collectors.toList());

        assertTrue("MARY".equals(result.get(0).getName()));
        assertTrue("DE NIRO".equals(result.get(1).getName()));
        assertEquals(6,gimmeListPerson().size());
    }

    @Test
    public void predicateTestInFunction(){
        StringPredicateFunction stringPredicateFunction = new StringPredicateFunction();
        List<String> result = Arrays.asList("Diego", "Daniel", "Mark","Dani");
        assertEquals(result,stringPredicateFunction.filter(gimmeListNames(),x -> x.startsWith("D") || x.length() == 4 ));
    }

    class StringPredicateFunction {
         List<String> filter(List<String> list, Predicate<String> predicate) {
            return list.stream().filter(predicate::test).collect(Collectors.toList());
        }
    }



    @Test
    public void predicateTestInFunction1(){

        TesteObjeto testeObjeto0 = new TesteObjeto();
        testeObjeto0.setIdade(305L);
        testeObjeto0.setNome("Diego");

        TesteObjeto testeObjeto1 = new TesteObjeto();
        testeObjeto1.setIdade(30L);
        testeObjeto1.setNome("Diegof");

        TesteObjeto testeObjeto2 = new TesteObjeto();
        testeObjeto2.setIdade(35L);
        testeObjeto2.setNome("Diegog");

        TesteObjeto testeObjeto3 = new TesteObjeto();
        testeObjeto3.setIdade(35L);
        testeObjeto3.setNome("Diegog");

        List<TesteObjeto> testeObjetos = new ArrayList<>();
        testeObjetos.add(testeObjeto0);
        testeObjetos.add(testeObjeto1);
        testeObjetos.add(testeObjeto2);
        testeObjetos.add(testeObjeto3);


        Set<String> nomes = new HashSet<>();
        testeObjetos.stream().forEach(teste -> nomes.add(teste.getNome()) );

        testeObjetos.size();



    }


    class TesteObjeto{
        String nome;
        Long idade;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Long getIdade() {
            return idade;
        }

        public void setIdade(Long idade) {
            this.idade = idade;
        }
    }




}