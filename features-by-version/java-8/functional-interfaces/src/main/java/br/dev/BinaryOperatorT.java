package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

/**
 * ✅ BinaryOperator<T>
 * A BinaryOperator<T> is a special case of BiFunction<T, T, T>
 * Takes two inputs of the same type T
 * Returns a result of the same type T
 * Used for combining or merging two values of the same type
 */
public class BinaryOperatorT {

    //to get
    Supplier<Post> randomPost = () -> {
        String title = "Post #" + new Random().nextInt(1000);
        String content = "Random content at " + new Date();
        return new Post(title, content, true);
    };


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Choose post with longer content
        BinaryOperator<Post> longerPost = (post1, post2) ->
                post1.getContent().length() >= post2.getContent().length() ? post1 : post2;

        Post longest = longerPost.apply(dataService.randomPost.get(), dataService.randomPost.get());
        System.out.println("Longer content post: " + longest.getTitle());

        // Merge post-titles (create summary post)
        BinaryOperator<Post> mergeTitles = (post1, post2) -> new Post(
                post1.getTitle() + " & " + post2.getTitle(),
                post1.getContent() + "\n---\n" + post2.getContent()
        );
        Post merged = mergeTitles.apply(dataService.randomPost.get(), dataService.randomPost.get());
        System.out.println("Merged post title: " + merged.getTitle());

        // Combine two usernames (e.g., co-authors)
        BinaryOperator<String> combineNames = (string, string2) -> string + " & " + string2;
        System.out.println("Combined authors: " + combineNames.apply("Alice", "Bob"));

        //Choose lexicographically greater title
        BinaryOperator<String> maxTitle = BinaryOperator.maxBy(Comparator.naturalOrder());
        System.out.println("Max title: " + maxTitle.apply(
                dataService.randomPost.get().getTitle(),
                dataService.randomPost.get().getTitle()
        ));

        // Choose post with shorter content (minBy)
        BinaryOperator<Post> shorterPost = BinaryOperator.minBy(
                Comparator.comparingInt(post -> post.getContent().length())
        );
        Post shorter = shorterPost.apply(dataService.randomPost.get(), dataService.randomPost.get());
        System.out.println("Shorter content post: " + shorter.getTitle());
    }

}
