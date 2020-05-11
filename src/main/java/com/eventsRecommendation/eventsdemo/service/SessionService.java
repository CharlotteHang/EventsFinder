package com.eventsRecommendation.eventsdemo.service;

import java.util.Calendar;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import com.eventsRecommendation.eventsdemo.dao.SessionDao;
import com.eventsRecommendation.eventsdemo.dao.UserDao;
import com.eventsRecommendation.eventsdemo.entity.Session;
import com.eventsRecommendation.eventsdemo.entity.User;
import com.eventsRecommendation.eventsdemo.filter.Context;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SessionService {
  private static final String COOKIE_SID = "SID";
  private static final int COOKIE_MAX_AGE = (int) (365.25 * 24 * 60 * 60);
  private static final int EXPIRSTION_TIME_IN_HOUR = 200;
  private static final String COOKIE_PATH = "/";

  SessionDao sessionDao;
  UserDao userDao;

  @Autowired
  public SessionService(SessionDao sessionDao, UserDao userDao) {
    this.sessionDao = sessionDao;
    this.userDao = userDao;
  }

  @Transactional
  public Session userLogin(User user) {
    HttpServletRequestWrapper request = Context.getThreadLocalHttpServletRequest();
    HttpServletResponseWrapper response = Context.getThreadLocalHttpServletResponse();
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (COOKIE_SID.equals(cookie.getName())) {
          deleteCookie(cookie, response);
      }
    }
    return addToNewSession(user);
  }
  @Transactional
  public Session addToNewSession(User user) {
    Session session = getNewSession();
    System.out.println("getSession");

    System.out.println("save session");
    userDao.save(addUserToSession(session, user));
    User savedUser = userDao.getUser(user.getEmail());
    session.setSessionUser(savedUser);
    sessionDao.save(session);
    return null;
  }

  private User addUserToSession(Session session, User user) {

    List<Session> sessions = user.getSessions();
    if(sessions == null) user.setSessions(ImmutableList.of(session));
    else sessions.add(session);
    return user;
  }


  private Session getNewSession() {
    Calendar nowDate = Calendar.getInstance();
    Session session = Session.builder().token(Session.getRandom())
    .dateAuthenticated(nowDate)
    .dateOnlineSince(nowDate)
    .build();
    System.out.println("authDate" + session.getDateAuthenticated());

    Cookie cookie = new Cookie(COOKIE_SID, session.getToken());
    cookie.setMaxAge(COOKIE_MAX_AGE);
    cookie.setPath(COOKIE_PATH);

    HttpServletResponseWrapper response = Context.getThreadLocalHttpServletResponse();
    response.addCookie(cookie);
    return session;
  }

  @Transactional
  public Session getSession() {
    Session session = getExistingSession();
    System.out.println("getExistingSession" + session);
    if (session == null || session.getDateAuthenticated() == null) {
      return null;
    } else {
      if (isTimeout(session)) {
        System.out.println("timeout");
        unauthenticate(session);
        return null;
      } else {
        Context.setThreadLocalSession(session);
        session.setDateOnlineSince(Calendar.getInstance());
        sessionDao.save(session);
        return session;
      }
    }
  }

//  @Transactional
//  public boolean isAuthenticated() {
//    Session session = getExistingSession();
//    System.out.println("isAuthenticated"+ session);
//    if (session == null || session.getDateAuthenticated() == null) {
//      return false;
//    } else {
//      if (isTimeout(session)) {
//        unauthenticate(session);
//        return false;
//      } else {
//        Context.setThreadLocalSession(session);
//        // also when signup and create an account
//        session.setDateOnlineSince(Calendar.getInstance());
//        sessionDao.save(session);
//        return true;
//      }
//    }
//  }

  private static boolean isTimeout(Session session) {
    if (session.getDateOnlineSince() == null) return true;
    Calendar onlineSince = session.getDateOnlineSince();
    System.out.println(onlineSince);
    onlineSince.add(Calendar.HOUR, EXPIRSTION_TIME_IN_HOUR);
    System.out.println(onlineSince);
    System.out.println(Calendar.getInstance());
    if (onlineSince.before(Calendar.getInstance())) return true;
    return false;
  }

  @Transactional
  public boolean unauthenticate() {
    Session session = getExistingSession();
    if (!session.isAuthenticated()) {
      log.warn("Session is not authenticated. Aborting logout.");
      return false;
    }
    unauthenticate(session);
    return true;
  }

  private void unauthenticate(Session session) {
    session.setDateAuthenticated(null);
    sessionDao.save(session);
  }

  private Session getExistingSession() {
    if(Context.getThreadLocalSession() != null) return Context.getThreadLocalSession();
    HttpServletRequestWrapper request = Context.getThreadLocalHttpServletRequest();
    HttpServletResponseWrapper response = Context.getThreadLocalHttpServletResponse();
    Cookie[] cookies = request.getCookies();
    System.out.println(request);
    for(Cookie cookie: cookies) {
      System.out.println(cookie.getName());
    }
    Session session = null;
    boolean sidCookieFound = false;

    if (request.getCookies() != null) {
      for (Cookie cookie : cookies) {
        if (COOKIE_SID.equals(cookie.getName())) {
          System.out.println("cookie" + cookie.getName());
//          if (sidCookieFound && response != null) {
//            deleteCookie(cookie, response);
//          } // duplicate cookie
//          sidCookieFound = true;  ?? how to implement

          String token = cookie.getValue();
          System.out.println("cookie" + token);
          session = sessionDao.getSession(token);
          System.out.println("cookie" + session);
          if ((session == null || session.getDateOnlineSince() == null) && response != null) {
            System.out.println("invalid cookie");
            deleteCookie(cookie, response);
            return null; // invalid cookie
          }
          System.out.println("return session1" + session);
          return session;
        }
      }
    }
    System.out.println("return session" + session);
    return session;
  }

  private static void deleteCookie(Cookie cookie, HttpServletResponse response) {
    // Delete the cookie with the following 2 lines
    cookie.setMaxAge(0);
    //        Context.getThreadLocalHttpServletResponse().addCookie(cookie);
    //        cookie.setDomain(domain);
    cookie.setPath(COOKIE_PATH); //??
    response.addCookie(cookie);
  }
}
