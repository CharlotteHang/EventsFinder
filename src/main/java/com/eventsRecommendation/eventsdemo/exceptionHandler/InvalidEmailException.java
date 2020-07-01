package com.eventsRecommendation.eventsdemo.exceptionHandler;

import java.util.Set;
import lombok.Getter;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message) {
        super(message);
    }
}