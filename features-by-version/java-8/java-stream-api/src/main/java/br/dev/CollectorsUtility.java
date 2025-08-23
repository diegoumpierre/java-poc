package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summarizingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class CollectorsUtility {


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. toList() – collect to List
        List<String> titlesList = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .collect(toList());
        System.out.println("toList(): " + titlesList);

        // 2. toSet() – collect to Set (remove duplicates)
        Set<String> titlesSet = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .collect(toSet());
        System.out.println("toSet(): " + titlesSet);

        // 3. toMap() – map title -> content (only first occurrence kept)
        Map<String, String> titleToContent = userList.get(0).getPosts().stream()
                .collect(toMap(Post::getTitle, Post::getContent, (existing, replacement) -> existing));
        System.out.println("toMap(): " + titleToContent);

        // 4. joining() – concatenate strings
        String joinedTitles = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .collect(joining(", "));
        System.out.println("joining(): " + joinedTitles);

        // 5. counting() – count elements
        long java8Count = userList.get(0).getPosts().stream()
                .filter(post -> post.getTitle().equals("Java 8"))
                .collect(counting());
        System.out.println("counting(): " + java8Count);

        // 6. groupingBy() – group by title
        Map<String, List<Post>> groupedByTitle = userList.get(0).getPosts().stream()
                .collect(groupingBy(Post::getTitle));
        System.out.println("groupingBy(): " + groupedByTitle);

        // 7. partitioningBy() – separate posts with title "Java 8"
        Map<Boolean, List<Post>> partitioned = userList.get(0).getPosts().stream()
                .collect(partitioningBy(post -> post.getTitle().equals("Java 8")));
        System.out.println("partitioningBy(): " + partitioned);

        // 8. summarizingInt() – statistics on title lengths
        IntSummaryStatistics titleLengthStats = userList.get(0).getPosts().stream()
                .collect(summarizingInt(post -> post.getTitle().length()));
        System.out.println("summarizingInt(): " + titleLengthStats);

        // 9. mapping() – transform before collecting
        Set<String> upperTitles = userList.get(0).getPosts().stream()
                .collect(mapping(post -> post.getTitle().toUpperCase(), toSet()));
        System.out.println("mapping(): " + upperTitles);

        // 10. reducing() – custom reduction
        String reducedTitles = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .collect(reducing("", (a, b) -> a.isEmpty() ? b : a + " | " + b));
        System.out.println("reducing(): " + reducedTitles);
    }
}
