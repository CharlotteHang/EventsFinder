package com.eventsRecommendation.eventsdemo.rest;


import java.util.regex.Pattern;
import com.eventsRecommendation.eventsdemo.service.SignupService;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usersignup")
public class SignupResource {
    private SignupService signupService;

    @Autowired
    public SignupResource(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    public JsonResponse<AuthenticateUserResponseData> signUp(
            @RequestBody SignupRequestPresentation signupRequest) {
        signupService.signupMember(signupRequest.email, signupRequest.password, signupRequest.firstName, signupRequest.lastName);

        return JsonResponse.of(AuthenticateUserResponseData.builder().logined(true).build()); // return token and userId??
    }

    @Value
    private static class SignupRequestPresentation {
        String email;
        String password;
        String firstName;
        String lastName;
    }

    @Data
    @Builder
    private static class AuthenticateUserResponseData {
        boolean logined;
    }
}
