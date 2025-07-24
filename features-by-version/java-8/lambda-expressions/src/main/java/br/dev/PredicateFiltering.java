package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateFiltering {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. Filter posts that have content
        Predicate<Post> hasContent = post -> post.getContent() != null
                && !post.getContent().trim().isEmpty();
        List<Post> withContent = userList.stream().flatMap(user -> {
            return user.getPosts().stream();
                })
                .filter(hasContent)
                .collect(Collectors.toList());
        System.out.println("Posts with content: " + withContent.size());

    }

}
