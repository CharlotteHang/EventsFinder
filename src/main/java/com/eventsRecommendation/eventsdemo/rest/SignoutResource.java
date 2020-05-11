package com.eventsRecommendation.eventsdemo.rest;

import com.eventsRecommendation.eventsdemo.entity.Session;
import com.eventsRecommendation.eventsdemo.exceptionHandler.NotAuthorizedException;
import com.eventsRecommendation.eventsdemo.filter.Context;
import com.eventsRecommendation.eventsdemo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usersignout")
public class SignoutResource {
    private SessionService sessionService;

    @Autowired
    public SignoutResource(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public JsonResponse<Void> signOut() {
    System.out.println("signout");
        Session session = sessionService.getSession();;
        if (session == null) {
            throw new NotAuthorizedException("Session is null");
        } else if (!session.isAuthenticated()) {
            throw new NotAuthorizedException("Not logged in");
        }

        Boolean result = sessionService.unauthenticate();
        if (!result) {
            throw new RuntimeException("Failed logging out");
        }
        return JsonResponse.of();
    }
}
