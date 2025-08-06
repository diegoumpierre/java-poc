package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IntermediateOperations {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. filter() – keep posts with "Java 8" title
        List<Post> postList = userList.get(0).getPosts().stream()
                .filter(post -> post.getTitle().equals("Java 8"))
                .collect(Collectors.toList());
        System.out.println("Filtered (Java 8): " + postList.size());

        // 2. map() – convert posts to titles
        List<String> titles = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .collect(Collectors.toList());
        System.out.println("Mapped titles: " + titles);

        // 3. distinct() – remove duplicate titles
        List<String> distinctTitles = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Distinct titles: " + distinctTitles);

        // 4. sorted() – sort by title alphabetically
        List<Post> sortedPosts = userList.get(0).getPosts().stream()
                .sorted(Comparator.comparing(Post::getTitle))
                .collect(Collectors.toList());
        System.out.println("Sorted titles:");
        sortedPosts.forEach(p -> System.out.println(" - " + p.getTitle()));

        // 5. limit() – only take first 2 posts
        List<Post> limited = userList.get(0).getPosts().stream()
                .limit(2)
                .collect(Collectors.toList());
        System.out.println("Limited to 2 posts: " + limited.size());

        // 6. skip() – skip the first 2 posts
        List<Post> skipped = userList.get(0).getPosts().stream()
                .skip(2)
                .collect(Collectors.toList());
        System.out.println("Skipped first 2: " + skipped.size());

        // 7. flatMap() – flatten posts from users
        List<Post> allPosts = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .collect(Collectors.toList());
        System.out.println("FlatMapped total posts: " + allPosts.size());
    }

}
