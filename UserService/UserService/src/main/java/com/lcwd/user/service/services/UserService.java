package com.lcwd.user.service.services;

import com.lcwd.user.service.entity.User;

import java.util.List;

public interface UserService {

    //create
    User saveUser(User user);

    //get all user
    List<User> getAllUser();

    //get user info
    User getUser(String userId);


    void deleteUser(String userId);


}
