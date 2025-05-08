package com.lcwd.user.service.controller;
import com.lcwd.user.service.entity.User;
import com.lcwd.user.service.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    //create
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){

        User user1 = userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    //single user get

    @GetMapping("/{user_id}")
    @CircuitBreaker(name="ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getSingleUser(@PathVariable("user_id") String userId){// if both userid are same then no need to add name in pathvariable

        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    //creating fallbackmethod for circuit breaker
    public ResponseEntity<User> ratingHotelFallback(String userId, Exception exception){
        logger.info("Fallback is executed, because service is down : "+exception.getMessage());
        User user = User.builder()
                    .email("dummy@gmail.com")
                    .name("Dummy")
                    .about("This use is created dummy because some services is down")
                    .userId("2134")
                    .build();
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    // get all user
    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        List<User> allUser = userService.getAllUser();
        return ResponseEntity.ok(allUser);

    }


}
