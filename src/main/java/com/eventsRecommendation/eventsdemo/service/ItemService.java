package com.eventsRecommendation.eventsdemo.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.eventsRecommendation.eventsdemo.dao.ItemDao;
import com.eventsRecommendation.eventsdemo.dao.UserDao;
import com.eventsRecommendation.eventsdemo.entity.Category;
import com.eventsRecommendation.eventsdemo.entity.Item;
import com.eventsRecommendation.eventsdemo.entity.Session;
import com.eventsRecommendation.eventsdemo.entity.User;
import com.eventsRecommendation.eventsdemo.exceptionHandler.NotAuthorizedException;
import com.eventsRecommendation.eventsdemo.external.TicketMasterApi;
import com.eventsRecommendation.eventsdemo.filter.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
  private ItemDao itemDao;
  private UserDao userDao;
  private SessionService sessionService;

  @Autowired
  public ItemService(ItemDao itemDao, UserDao userDao, SessionService sessionService) {
    this.itemDao = itemDao;
    this.userDao = userDao;
    this.sessionService = sessionService;
  }

  @Transactional
  public List<Item> searchItems(double lat, double lon, String term) {
    List<Item> items = TicketMasterApi.search(lat, lon, term);
    for (Item item : items) {
      itemDao.save(item);
    }
    return items;
  }

  public List<Item> getFavouriteItems() {
    Session session = sessionService.getSession();
    if (session == null) throw new NotAuthorizedException("User has not been authenticated");
    User user = session.getSessionUser();
    //    if (user == null) throw new NotAuthorizedException("User has not been authenticated");
    List<Item> items = user.getItems();
    for (Item item : items) item.setFavorite(true);
    return items;
  }

  @Transactional
  public void likeItem(boolean likeItem, String itemId) {
    Session session = sessionService.getSession();
    if (session == null) throw new NotAuthorizedException("User has not been authenticated");
    User user = session.getSessionUser();
        if (user == null) throw new NotAuthorizedException("User has not been authenticated");

    Item item = itemDao.getItem(itemId);
    if (item == null) throw new RuntimeException("Cannot find the item with id " + itemId);
    List<Item> items = user.getItems();
    Set<User> users = item.getUsers();
    System.out.println(items.size());

    if (likeItem) {
      users.add(user);
      //items.add(item) : this will cause exception: Data truncated for column 'user_id' at row 1
    } else {
      users.remove(user);
    }
//    System.out.println(items.size());
//    System.out.println(user.getUserId());
      itemDao.save(item);
  }

  @Transactional
  public List<Item> getRecommendedItems(double lat, double lon) {
    Session session = sessionService.getSession();
    if (session == null) throw new NotAuthorizedException("User has not been authenticated");
    User user = session.getSessionUser();
    //        if (user == null) throw new NotAuthorizedException("User has not been authenticated");

    List<Item> favouriteItems = user.getItems();
    Map<Category, Integer> allCategories = new HashMap<>();
    for (Item item : favouriteItems) {
      Set<Category> categories = item.getCategories();
      for (Category category : categories) {
        allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
      }
    }

    List<Map.Entry<Category, Integer>> categoryList =
        new ArrayList<Entry<Category, Integer>>(allCategories.entrySet());
    Collections.sort(
        categoryList,
        ((category1, category2) -> Integer.compare(category2.getValue(), category1.getValue())));

    Set<Item> visitedItems = new HashSet<>();
    List<Item> recommendedItems = new ArrayList<>();
    for (Map.Entry<Category, Integer> category : categoryList) {
      List<Item> items = searchItems(lat, lon, category.getKey().getCategoryType());
      List<Item> filteredItems = new ArrayList<>();
      for (Item item : items) {
        if (!favouriteItems.contains(item) && !visitedItems.contains(item)) {
          filteredItems.add(item);
        }
      }
      Collections.sort(
          filteredItems,
          ((item1, item2) -> Double.compare(item2.getDistance(), item1.getDistance())));

      visitedItems.addAll(items);
      recommendedItems.addAll(filteredItems); // debug: items
    }
    return recommendedItems;
  }
}
