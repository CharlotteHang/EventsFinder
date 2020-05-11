package com.eventsRecommendation.eventsdemo.service;

import com.eventsRecommendation.eventsdemo.dao.UserDao;
import com.eventsRecommendation.eventsdemo.entity.User;
import com.eventsRecommendation.eventsdemo.exceptionHandler.NotAuthorizedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    UserDao userDao;
    SessionService sessionService; //

    @Autowired
    public LoginService(SessionService sessionService, UserDao userDao) {
        this.userDao = userDao;
        this.sessionService = sessionService;
    }

    @Transactional
    public User authenticate(String email, String password) {
        User user = userDao.verifyLogin(email, password);
        if(user == null) throw new NotAuthorizedException("Invalid email or password");
        sessionService.addToNewSession(user);
        return user;
    }
}
