package com.eventsRecommendation.eventsdemo.service;

import com.eventsRecommendation.eventsdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService {

  UserService userService;
  SessionService sessionService; //

  @Autowired
  public SignupService(UserService userService, SessionService sessionService) {
    this.userService = userService;
    this.sessionService = sessionService;
  }

  @Transactional
  public User signupMember(String email, String password, String firstName, String lastName) {
    User user = userService.createUser(email, password, firstName, lastName);
    sessionService.addToNewSession(user);
    return user;
  }
}
