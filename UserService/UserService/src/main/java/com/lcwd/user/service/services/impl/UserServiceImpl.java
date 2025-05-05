package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entity.Hotel;
import com.lcwd.user.service.entity.Rating;
import com.lcwd.user.service.entity.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserID = UUID.randomUUID().toString(); // unique user id
        user.setUserId(randomUserID);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User with given id is not found on server !! : "+ userId));
        // get user from database with the help of user repository
        //http://localhost:8083/ratings/users/57ac8840-9725-4dca-bdb8-e3ec40bb627b

        Rating[] ratingOfUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+user.getUserId(), Rating[].class);
        logger.info("{}",ratingOfUser);
        //conversion of array into list
        List<Rating> ratings = Arrays.stream(ratingOfUser).toList(); // both will work
      //  List<Rating> ratingList = ratings.stream().map(rating->{
      List<Rating> ratingList = Arrays.stream(ratingOfUser).map(rating->{
           //api call
     // using RestTemplate      // ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotels/" + rating.getHotelId(), Hotel.class);
                                // rating.setHotel(forEntity.getBody());

          //using feignclient
            Hotel hotel = hotelService.getHotelAA(rating.getHotelId());
            rating.setHotel(hotel);
            //set the hotel of rating



           //return rating
           return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);


        return user;
    }

    @Override
    public void deleteUser(String userId) {

    }
}
