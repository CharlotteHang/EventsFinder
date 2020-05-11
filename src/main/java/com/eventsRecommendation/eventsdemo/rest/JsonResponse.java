package com.eventsRecommendation.eventsdemo.rest;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class JsonResponse<E> {

    E data;

    @Singular
    Set<Object> errors;

    Set<Object> missingFields;

    @Builder.Default
    transient int statusCode = Response.Status.OK.getStatusCode();

    @Singular
    transient Map<String, Object> headers;

    public static <E> JsonResponse<E> of(Response.Status status, E data) {
        return JsonResponse.<E>builder()
                .statusCode(status.getStatusCode())
                .data(data)
                .build();
    }

    public static JsonResponse<Void> of(Response.Status status) {
        return of(status, null);
    }

    public static JsonResponse<Void> of() {
        return of(Response.Status.OK, null);
    }

    public static <E> JsonResponse<E> of(E data) {
        return of(Response.Status.OK, data);
    }
}

