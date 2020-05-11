package com.eventsRecommendation.eventsdemo.filter;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import com.eventsRecommendation.eventsdemo.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class ContextFilter implements Filter {
  // ThreadLocalSession ？？
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    // Store headers
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    logRequestInfo(httpRequest);

    HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest);
    Context.setThreadLocalHttpServletRequest(requestWrapper);
    System.out.println("contextFilter" + httpRequest.getCookies());
    System.out.println("contextFilter" + requestWrapper.getCookies());
    HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper((HttpServletResponse) response);
    Context.setThreadLocalHttpServletResponse(responseWrapper);


    //        if (FilterPaths.API_IGNORED_PATHS.contains(httpRequest.getRequestURI())) {
    //            filterChain.doFilter(request, response);
    //        }
    responseWrapper.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
    responseWrapper.addHeader("Access-Control-Allow-Credentials", "true");
    responseWrapper.addHeader("Access-Control-Allow-Methods", "*");
    filterChain.doFilter(requestWrapper, responseWrapper);
    Context.reset();
    response.setContentType("application/json");// tell it it is a json
//    responseWrapper.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");//'*': anywebsite can have access to our api
    responseWrapper.addHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");


  }

  private void logRequestInfo(HttpServletRequest request) {
    log.debug(
        new StringBuilder()
            .append("HttpServletRequest:\n")
            .append("contextPath: ")
            .append(request.getContextPath())
            .append("\n")
            .append("pathInfo: ")
            .append(request.getPathInfo())
            .append("\n")
            .append("pathTranslated: ")
            .append(request.getPathTranslated())
            .append("\n")
            .append("queryString: ")
            .append(request.getQueryString())
            .append("\n")
            .append("requestURI: ")
            .append(request.getRequestURI())
            .append("\n")
            .append("authType: ")
            .append(request.getAuthType())
            .append("\n")
            .append("remoteUser: ")
            .append(request.getRemoteUser())
            .append("\n")
            .append("servletPath: ")
            .append(request.getServletPath())
            .append("\n")
            .append("remoteAddr: ")
            .append(request.getRemoteAddr())
            .append("\n")
            .append("contentType: ")
            .append(request.getContentType())
            .append("\n")
            .append("requestURL: ")
            .append(request.getRequestURL())
            .append("\n")
            .append("contentLength: ")
            .append(request.getContentLength())
            .toString());

    StringBuilder sbHeaders = new StringBuilder();
    sbHeaders.append("Headers:\n");
      Enumeration<String> headers = request.getHeaderNames();
    while(headers.hasMoreElements()) {
        String headerName = headers.nextElement();
        String headerValue = request.getHeader(headerName);
      if (headerValue != null) {
        sbHeaders.append(headerName).append(": ").append(headerValue).append("\n");
      }
    }
    log.debug(sbHeaders.toString());

    Enumeration<String> parameterNames = request.getParameterNames();
    if (parameterNames.hasMoreElements() && request.getMethod().equals("GET")) {
      StringBuilder sbParameters = new StringBuilder();
      sbParameters.append("Parameters:\n");
      while (parameterNames.hasMoreElements()) {
        String parameterName = parameterNames.nextElement();
        String parameterValue = request.getParameter(parameterName);
        sbParameters.append(parameterName).append(": ").append(parameterValue).append("\n");
      }
      log.debug(sbParameters.toString());
    }
  }
}
