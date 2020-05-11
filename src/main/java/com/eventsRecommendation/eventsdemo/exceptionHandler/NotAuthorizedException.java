package com.eventsRecommendation.eventsdemo.exceptionHandler;

public class NotAuthorizedException extends RuntimeException{

  public NotAuthorizedException(String message) {
    super(message);
  }
}
