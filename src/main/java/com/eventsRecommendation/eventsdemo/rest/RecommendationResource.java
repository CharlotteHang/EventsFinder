package com.eventsRecommendation.eventsdemo.rest;

import java.util.List;
import com.eventsRecommendation.eventsdemo.entity.Item;
import com.eventsRecommendation.eventsdemo.service.ItemService;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/RecommendationsByLocation")
public class RecommendationResource {
    private ItemService itemService;

    @Autowired
    public RecommendationResource(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public JsonResponse<List<Item>> getRecommendedItems(
            @RequestParam(value = "lat") double lat,
            @RequestParam(value = "lon") double lon) {
        return JsonResponse.of(itemService.getRecommendedItems(lat, lon));
    }
}
