package com.eventsRecommendation.eventsdemo.service;

import java.util.regex.Pattern;
import com.eventsRecommendation.eventsdemo.entity.User;
import com.eventsRecommendation.eventsdemo.exceptionHandler.InvalidEmailException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");
  private static final int WORKLOAD = 14;
  UserService userService;
  SessionService sessionService; //

  @Autowired
  public SignupService(UserService userService, SessionService sessionService) {
    this.userService = userService;
    this.sessionService = sessionService;
  }

  @Transactional
  public User signupMember(String email, String password, String firstName, String lastName) {
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new InvalidEmailException("Incorrect email");
    }
    User user = userService.createUser(email, hashPassword(password), firstName, lastName);
    sessionService.addToNewSession(user);
    return user;
  }

  //salt
  private String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt(WORKLOAD));
  }

}
