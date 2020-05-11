package com.eventsRecommendation.eventsdemo.filter;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.core.Response.Status;
import com.eventsRecommendation.eventsdemo.exceptionHandler.NotAuthorizedException;
import com.eventsRecommendation.eventsdemo.rest.JsonResponse;
import com.eventsRecommendation.eventsdemo.service.SessionService;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@Order(2)
@Component
// @WebFilter(urlPatterns = {}, filterName = "securityRequestFilter")
// webFilter not working, it also filters the nearby page which does not need authentication remove
// @component?
public class AuthenticationFilter implements Filter {

  SessionService sessionService;

  private static final Set<String> urlPatterns = Collections.emptySet();
//      ImmutableSet.of("/favouriteItems", "/RecommendationsByLocation", "/usersignout");

//  @Autowired
//  public AuthenticationFilter(SessionService sessionService) {
//    this.sessionService = sessionService;
//  }
//
//  //    abstract void checkSessionAuthentication(ServletRequest request, ServletResponse response,
//  // SessionV3 session);
//
//  public void init(FilterConfig filterConfig) throws ServletException {
//
//        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
////    ServletContext servletContext = filterConfig.getServletContext();
////    WebApplicationContext webApplicationContext =
////            WebApplicationContextUtils.getWebApplicationContext(servletContext);
////
////    AutowireCapableBeanFactory autowireCapableBeanFactory =
////            webApplicationContext.getAutowireCapableBeanFactory();
////
////    autowireCapableBeanFactory.configureBean(this, "SessionService");
//  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

//    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//      if(sessionService==null){
//        ServletContext servletContext = request.getServletContext();
//        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
//        sessionService = webApplicationContext.getBean(SessionService.class);
//      }

    HttpServletRequestWrapper requestWrapper = Context.getThreadLocalHttpServletRequest();
    HttpServletResponseWrapper responseWrapper = Context.getThreadLocalHttpServletResponse();
    System.out.println("authFilterCookie" + requestWrapper.getCookies());

    try {
      String path = requestWrapper.getRequestURI();
      System.out.println(path);
      if (urlPatterns.contains(path)) {
        checkAuthentication();
      }
      System.out.println("aftercheckauthentication");
      filterChain.doFilter(request, response);
    } catch (NotAuthorizedException e) {
      System.out.println("notauthorizedException");
      System.out.println(e.getStackTrace());
      responseWrapper.setStatus(Status.UNAUTHORIZED.getStatusCode());
      //            responseWrapper.setContentType(ContentType.APPLICATION_JSON.getMimeType());
      responseWrapper
          .getWriter()
          .println(new Gson().toJson(JsonResponse.builder().error(e).build()));
    }
  }

  //    private final String[] publicPaths;
  //    private final String[] publicPostPaths;
  //
  //    private boolean shouldSkip(HttpServletRequest httpServletRequest) {
  //        String uri = httpServletRequest.getRequestURI();
  //        if (StringUtils.startsWithAny(uri, publicPaths)) {
  //            return true;
  //        }
  //        if (httpServletRequest.getMethod().equals(HttpMethod.POST) && StringUtils.equalsAny(uri,
  // publicPostPaths)) {
  //            log.debug("Skipping authentication for POST to {}", uri);
  //            return true;
  //        }
  //        return false;
  //    }

  private void checkAuthentication() {
    // CSRF protection mechanism recommended by OWASP
    // https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html
    //        if (requestWrapper.getMethod().equals(HttpMethod.POST)) {
    //            try {
    //                URL origin = new
    // URL(ObjectUtils.firstNonNull(requestWrapper.getHeader(HttpHeaders.ORIGIN),
    // requestWrapper.getHeader(HttpHeaders.REFERER)));
    //                String host = requestWrapper.getHeader(HttpHeaders.HOST);
    //                if (isInvalidOrigin(origin, host)) {
    //                    throw new NotAuthorizedException("Cross origin request forgery detected");
    //                }
    //            } catch (MalformedURLException e) {
    //                throw new NotAuthorizedException("Malformed Origin header");
    //            }
    //        }
    if (sessionService.getSession() == null) {
      throw new NotAuthorizedException("Session is not authenticated");
    }
  }

  private boolean isInvalidOrigin(URL origin, String host) {
    if (origin.getPort() == -1) {
      return !host.equals(origin.getHost());
    }
    return !host.equals(origin.getHost() + ":" + origin.getPort());
  }
}
