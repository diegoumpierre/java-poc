package com.poc.springsecurity.service;

import com.poc.springsecurity.repository.TweetRepository;
import com.poc.springsecurity.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;


    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }



}
