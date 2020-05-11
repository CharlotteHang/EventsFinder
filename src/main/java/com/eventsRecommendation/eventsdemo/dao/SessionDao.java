package com.eventsRecommendation.eventsdemo.dao;

import javax.persistence.EntityManager;
import com.eventsRecommendation.eventsdemo.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SessionDao {
    private EntityManager entityManager;

    @Autowired
    public SessionDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Session getSession(String token) {
        if(StringUtils.isBlank(token)) return null;
        org.hibernate.Session currentSession = entityManager.unwrap(org.hibernate.Session.class);// get current hibernate session
    System.out.println("toen" + token);
        Session session = currentSession.get(Session.class, token);
        return session;
    }

    public Session save(Session session) {
        org.hibernate.Session currentSession = entityManager.unwrap(org.hibernate.Session.class);// get current hibernate session
        currentSession.saveOrUpdate(session);
        return session;
    }


}

