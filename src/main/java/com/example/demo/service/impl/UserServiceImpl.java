package com.example.demo.service.impl;


import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String findUserNameByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(User::getUserName)
                .orElse("匿名ユーザー"); // ユーザーが見つからない場合
    }
}