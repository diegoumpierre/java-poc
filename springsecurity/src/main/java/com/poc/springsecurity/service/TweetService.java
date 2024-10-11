package com.poc.springsecurity.service;

import com.poc.springsecurity.controller.dto.FeedDto;
import com.poc.springsecurity.controller.dto.FeedItemDto;
import com.poc.springsecurity.entity.Role;
import com.poc.springsecurity.entity.Tweet;
import com.poc.springsecurity.repository.TweetRepository;
import com.poc.springsecurity.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;


    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    public FeedDto findAll(int page, int pageSize) {
        var tweets = tweetRepository.findAll(
                        PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdTimestamp"))
                .map(tweet ->
                        new FeedItemDto(
                                tweet.getTweetId(),
                                tweet.getContent(),
                                tweet.getUser().getUsername()
                        ));
        return new FeedDto(
                tweets.getContent(),
                page,
                pageSize,
                tweets.getTotalPages(),
                tweets.getTotalElements());
    }

    public void createNew(String userId, String content) throws Exception {
        var user = userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty()) throw new Exception("User don't exists");
        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(content);
        tweetRepository.save(tweet);

    }
}
