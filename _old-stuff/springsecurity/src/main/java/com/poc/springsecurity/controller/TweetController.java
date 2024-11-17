package com.poc.springsecurity.controller;

import com.poc.springsecurity.controller.dto.CreateTweetDto;
import com.poc.springsecurity.controller.dto.FeedDto;
import com.poc.springsecurity.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return ResponseEntity.ok(tweetService.findAll(page,pageSize));
    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto dto,
                                            JwtAuthenticationToken token) throws Exception {
        tweetService.createNew(token.getName(), dto.content());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,
                                            JwtAuthenticationToken token) {
        if (tweetService.deleteTweet(token.getName(), tweetId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }
}