package com.eventsRecommendation.eventsdemo.dao;

import javax.persistence.EntityManager;
import com.eventsRecommendation.eventsdemo.entity.Item;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ItemDao {
    private EntityManager entityManager; // automatically created by srping boot so we can inject it here

    @Autowired
    public ItemDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // so no need to manually start and commit the transactions, leave to service @Transactional

    public void save(Item item) {
        Session currentSession = entityManager.unwrap(Session.class);
        if(getItem(item.getItemId())== null) currentSession.saveOrUpdate(item);

    }

    public Item getItem(String itemId) {
        Session currentSession = entityManager.unwrap(Session.class);// get current hibernate session
        return currentSession.get(Item.class, itemId);

    }


}

