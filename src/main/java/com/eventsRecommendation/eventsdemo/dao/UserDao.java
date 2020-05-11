package com.eventsRecommendation.eventsdemo.dao;

import java.util.List;
import javax.persistence.EntityManager;
import com.eventsRecommendation.eventsdemo.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private EntityManager entityManager;

    @Autowired
    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User save(User user) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.saveOrUpdate(user);
        return user;
    }

    public boolean isRegisteredEmail(String email){
        return getUser(email) != null;
    }

    public User verifyLogin(String email, String password) {
        User user = getUser(email);

        if(user != null  && user.getPassword().equals(password)) return user;
        return null;
    }

    public User getUser(String email) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query query = currentSession.createQuery("from User u where u.email = :email ")
                .setParameter("email", email);
        List<User> users =  query.list();
        if(users == null || users.size() == 0) return null;
        else return users.get(0);
    }

}
