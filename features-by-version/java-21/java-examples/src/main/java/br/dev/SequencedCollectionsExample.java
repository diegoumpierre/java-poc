package br.dev;

import br.dev.domain.Post;
import br.dev.domain.User;
import java.util.SequencedSet;
import java.util.SequencedMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;

public class SequencedCollectionsExample {
    public static void main(String[] args) {
        // SequencedSet example
        SequencedSet<Post> postSet = (SequencedSet<Post>) new LinkedHashSet<>();
        postSet.add(new Post("First Post", "Content 1", true));
        postSet.add(new Post("Second Post", "Content 2", false));
        postSet.add(new Post("Third Post", "Content 3", true));

        System.out.println("First post in set: " + postSet.getFirst().getTitle());
        System.out.println("Last post in set: " + postSet.getLast().getTitle());

        // SequencedMap example
        SequencedMap<String, User> userMap = (SequencedMap<String, User>) new LinkedHashMap<>();
        userMap.put("Diego", new User("Diego Garcia", "diego@umpierre.com"));
        userMap.put("Joao", new User("Joao Melo", "joao@umpierre.com"));
        userMap.put("Lidia", new User("Lidia Umpierre ", "lidia@umpierre.com"));

        System.out.println("First user in map: " + userMap.getFirstEntry().getValue().getName());
        System.out.println("Last user in map: " + userMap.getLastEntry().getValue().getName());
    }
}

