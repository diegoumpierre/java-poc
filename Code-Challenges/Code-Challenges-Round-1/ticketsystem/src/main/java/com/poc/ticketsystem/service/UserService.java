package com.poc.ticketsystem.service;

import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> getAll(){
        return (List<User>) userRepository.findAll();
    }




}
