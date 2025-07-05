package br.dev;

import java.util.LinkedHashMap;
import java.util.SequencedMap;

public class SequencedCollections {

    private void something(){

        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        System.out.println(map.firstEntry()); // A=1
        System.out.println(map.lastEntry());  // B=2
    }

}
