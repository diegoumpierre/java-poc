package br.com.poc;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class Jep356 {

    public static void main(String[] args) {


        System.out.println("The default RandonGenerator algorithm: "+RandomGenerator.getDefault().getClass().getName());


        // legacy
        // RandomGeneratorFactory.of("Random").create(42);

        // default L32X64MixRandom
        // RandomGenerator randomGenerator = RandomGeneratorFactory.getDefault().create();

        // Passing the same seed to random, and then calling it will give you the same set of numbers
        // for example, seed = 999
        RandomGenerator randomGenerator = RandomGeneratorFactory.of("Xoshiro256PlusPlus").create(999);

        System.out.println(randomGenerator.getClass());

        int counter = 0;
        while(counter<=10){
            // 0-10
            int result = randomGenerator.nextInt(11);
            System.out.println(result);
            counter++;
        }



        RandomGeneratorFactory.all()
                .map(fac -> fac.group()+ " : " +fac.name())
                .sorted()
                .forEach(System.out::println);


    }

}
