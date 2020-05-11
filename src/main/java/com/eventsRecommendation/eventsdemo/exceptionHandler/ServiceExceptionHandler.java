package com.eventsRecommendation.eventsdemo.exceptionHandler;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.eventsRecommendation.eventsdemo.rest.JsonResponse;
import com.google.common.collect.ImmutableMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Map<Class<?>, HttpStatus> STATUS_CODES = ImmutableMap.of(
            MissingParameterException.class, HttpStatus.BAD_REQUEST,
            NotAuthorizedException.class, HttpStatus.UNAUTHORIZED
    );

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    ResponseEntity<JsonResponse> handleControllerException(HttpServletRequest req, Throwable exception) {
        JsonResponse.JsonResponseBuilder responseBuilder = JsonResponse.builder();
        if(exception instanceof MissingParameterException) {
            responseBuilder.missingFields(((MissingParameterException) exception).getMissingParameters());
        }
        int statusCode = STATUS_CODES.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR).value();
        return new ResponseEntity<>(responseBuilder.statusCode(statusCode).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
