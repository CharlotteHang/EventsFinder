package com.eventsRecommendation.eventsdemo.service;

import java.util.HashSet;
import java.util.Set;
import com.eventsRecommendation.eventsdemo.dao.SessionDao;
import com.eventsRecommendation.eventsdemo.dao.UserDao;
import com.eventsRecommendation.eventsdemo.entity.User;
import com.eventsRecommendation.eventsdemo.exceptionHandler.MissingParameterException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    UserDao userDao;

    @Autowired
    public UserService(SessionDao sessionDao, UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public User createUser(String email, String password, String firstName, String lastName) {
        Set<Object> missingParameters = new HashSet<>();

        // change this to error/errorCode??
        if (StringUtils.isBlank(email)) {
            missingParameters.add(email);
        }
        if (StringUtils.isBlank(password)) {
            missingParameters.add(password);
        }
        if (StringUtils.isBlank(firstName)) {
            missingParameters.add(firstName);
        }
        if (StringUtils.isBlank(lastName)) {
            missingParameters.add(lastName);
        }

        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException("Missing fields in the request body", missingParameters);
        }

        if(userDao.isRegisteredEmail(email)) throw new RuntimeException("The email has been registered");

        User user = new User(email, password, firstName, lastName);
        userDao.save(user);
        return user;
    }

    @Transactional
    public User getUser(String email) {
return userDao.getUser(email);
    }
}
