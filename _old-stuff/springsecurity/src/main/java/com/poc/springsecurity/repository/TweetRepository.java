package com.poc.springsecurity.repository;

import com.poc.springsecurity.entity.Tweet;
import com.poc.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
}
