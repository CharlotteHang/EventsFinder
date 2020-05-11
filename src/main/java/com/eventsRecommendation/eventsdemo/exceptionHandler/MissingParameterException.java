package com.eventsRecommendation.eventsdemo.exceptionHandler;

import java.util.Set;
import lombok.Getter;

public class MissingParameterException extends RuntimeException{
    @Getter
    private Set<Object> missingParameters;
    public MissingParameterException(String message, Set<Object> missingParameters) {
        super(message);
        this.missingParameters = missingParameters;
    }
}
