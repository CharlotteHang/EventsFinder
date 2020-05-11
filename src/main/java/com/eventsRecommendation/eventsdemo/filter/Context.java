package com.eventsRecommendation.eventsdemo.filter;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;
import com.eventsRecommendation.eventsdemo.entity.Session;

public class Context {
    private static final ThreadLocal<HttpServletRequestWrapper> threadLocalRequest = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponseWrapper> threadLocalResponse = new ThreadLocal<>();
    private static final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();

    public static void reset() {
        // Because ThreadLocal has Map of currentThread and value,
        // If you just set the value to null, the key still exists and it will create a memory leak.
        threadLocalRequest.remove();
        threadLocalResponse.remove();
        threadLocalSession.remove();
    }

    public static HttpServletRequestWrapper getThreadLocalHttpServletRequest() {
        return threadLocalRequest.get();
    }

    public static void setThreadLocalHttpServletRequest(HttpServletRequestWrapper requestWrapper) {
    System.out.println("set Request");
    System.out.println("26" + requestWrapper.getCookies());
        threadLocalRequest.set(requestWrapper);
    }

    public static HttpServletResponseWrapper getThreadLocalHttpServletResponse() {
        return threadLocalResponse.get();
    }

    public static void setThreadLocalHttpServletResponse(HttpServletResponseWrapper responseWrapper) {
        threadLocalResponse.set(responseWrapper);
    }

    public static Session getThreadLocalSession() {
        return threadLocalSession.get();
    }

    public static void setThreadLocalSession(Session threadLocalSession) {
        Context.threadLocalSession.set(threadLocalSession);
    }
}
