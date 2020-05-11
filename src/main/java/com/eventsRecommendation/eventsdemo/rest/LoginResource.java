package com.eventsRecommendation.eventsdemo.rest;

import com.eventsRecommendation.eventsdemo.entity.Session;
import com.eventsRecommendation.eventsdemo.entity.User;
import com.eventsRecommendation.eventsdemo.service.LoginService;
import com.eventsRecommendation.eventsdemo.service.SessionService;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userlogin")
public class LoginResource {
  private LoginService loginService;
  private SessionService sessionService;

  @Autowired
  public LoginResource(LoginService loginService, SessionService sessionService) {
    this.loginService = loginService;
    this.sessionService = sessionService;
  }

  @GetMapping
  public JsonResponse<AuthenticateUserResponseData> check() {

    Session session = sessionService.getSession();
    System.out.println("checkSession" + session);
    if (session == null) return JsonResponse.of(AuthenticateUserResponseData.builder()
            .logined(false)
            .build());

    User user = session.getSessionUser();
    return JsonResponse.of(AuthenticateUserResponseData.builder()
            .logined(true)
            .userId(user.getUserId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build()); // return token and userId??
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public JsonResponse<AuthenticateUserResponseData> login(
      @RequestBody LoginRequestPresentation loginRequest) {
    System.out.println("login email" + loginRequest.email);
    User user = loginService.authenticate(loginRequest.email, loginRequest.password);

    return JsonResponse.of(
        AuthenticateUserResponseData.builder()
            .logined(true)
            .userId(user.getUserId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build()); // return token and userId??
  }

  @Data
  @Builder
  private static class AuthenticateUserResponseData {
    boolean logined;
    int userId;
    String firstName;
    String lastName;
  }

  @Value
  private static class LoginRequestPresentation {
    String email;
    String password;
  }
}
