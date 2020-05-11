package com.eventsRecommendation.eventsdemo.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.eventsRecommendation.eventsdemo.dao.ItemDao;
import com.eventsRecommendation.eventsdemo.entity.Item;
import com.eventsRecommendation.eventsdemo.entity.Session;
import com.eventsRecommendation.eventsdemo.entity.User;
import com.eventsRecommendation.eventsdemo.filter.Context;
import com.eventsRecommendation.eventsdemo.service.ItemService;
import com.eventsRecommendation.eventsdemo.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/search-nearby")
public class ItemsResource {
  private ItemService itemService;
  private SessionService sessionService;

  @Autowired
  public ItemsResource(ItemService itemService, SessionService sessionService) {
    this.itemService = itemService;
    this.sessionService = sessionService;
  }

  @GetMapping
  public JsonResponse<List<Item>> getItem(@RequestParam(value="lat") double lat,
                                          @RequestParam(value="lon") double lon,
                                          @RequestParam(value="keyword",required = false,defaultValue = "") String keyword) {
    log.info("nearby endpoint");
    List<Item> allItems =
        itemService.searchItems(lat, lon, keyword);

    Session session = sessionService.getSession();
    if(session!=null) {
      User user = session.getSessionUser();
      List<Item> favoriteItems  = user.getItems();

      for (Item item : allItems) {
        if (favoriteItems.contains(item)) {
          item.setFavorite(true);
        }
      }
    }

    return JsonResponse.of(allItems);
  }
}
