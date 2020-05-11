package com.eventsRecommendation.eventsdemo.rest;

import java.util.List;
import com.eventsRecommendation.eventsdemo.entity.Item;
import com.eventsRecommendation.eventsdemo.service.ItemService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favouriteItems")
public class FavouriteItemResource {
    private ItemService itemService;

    @Autowired
    public FavouriteItemResource(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public JsonResponse<Void> likeItem(@RequestBody LikeItemRequestPresentation likeItemRequest) {
        itemService.likeItem(likeItemRequest.likeItem,likeItemRequest.itemId);
        return JsonResponse.of();
    }

    @GetMapping
    public JsonResponse<List<Item>> getFavouriteItem() {
        return JsonResponse.of(itemService.getFavouriteItems());
    }

    @Value
    private static class LikeItemRequestPresentation {
        boolean likeItem;
        String itemId;
    }
}
