/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.user.impl;

import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.user.Arq;
import com.btxtech.game.services.user.ArqEnum;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private com.btxtech.game.services.connection.Session session;
    private HibernateTemplate hibernateTemplate;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    private Map<DbBotConfig, UserState> botStates = new HashMap<DbBotConfig, UserState>();
    private final Collection<UserState> userStates = new ArrayList<UserState>();
    private Log log = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public boolean login(String name, String password) {
        /* TODO if (getUser().isLoggedIn()) {
           throw new IllegalStateException("The user is already logged in: " + getUser());
       }

       User user = getUser(name);
       if (user == null) {
           return false;
       }
       if (user.getPassword().equals(password)) {
           loginUser(user, false);
           return true;
       } else {
           return false;
       } */
        return true;
    }

    private void loginUser(User user, boolean keepGame) {
        /* TODO if (keepGame) {
            baseService.onUserRegistered();
        }
        userState.setLoggedIn();
        user.setLastLoginDate(new Date());
        save(user);
        try {
            userTrackingService.onUserLoggedIn(user, baseService.getBase());
        } catch (NoConnectionException e) {
            // Ignore
            userTrackingService.onUserLoggedIn(user, null);
        } */
    }

    @Override
    public User getUser() {
        return null;
        /* TODO User user = session.getUser();
      if (user == null) {
          user = new User();
          userGuidanceService.setLevelForNewUser(user);
          session.setUser(user);
      }
      return user; */
    }

    @Override
    public void logout() {
        /* TODO if (!getUser().isLoggedIn()) {
            throw new IllegalStateException("The user is not logged in: " + getUser());
        }

        getUser().setLoggedIn(false);
        session.setUser(null);*/
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getUser(final String name) {
        Object result = hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(User.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("name", name));
                return criteria.list();
            }
        });
        List<User> users = (List<User>) result;
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    @Override
    public void save(User user) {
        hibernateTemplate.saveOrUpdate(user);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAllUsers() {
        Object result = hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(User.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
        return (List<User>) result;
    }

    @Override
    public void createUserAndLoggin(String name, String password, String confirmPassword, String email, boolean keepGame) throws UserAlreadyExistsException, PasswordNotMatchException {
        /* TODO User user = getUser();
        if(user.isLoggedIn()) {
            throw new IllegalStateException("The user is already logged in: " + getUser());
        }

        if (getUser(name) != null) {
            throw new UserAlreadyExistsException();
        }

        if (!password.equals(confirmPassword)) {
            throw new PasswordNotMatchException();
        }
        user.registerUser(name, password, email);
        userTrackingService.onUserCreated(user);
        loginUser(user, keepGame);
        save(user);*/
    }

    @Override
    public boolean isAuthorized(ArqEnum arq) {
        // TODO  return getUser().hasArq(getArq(arq));
        return false;
    }

    @Override
    public void checkAuthorized(ArqEnum arq) {
        /* TODO if (!isAuthorized(arq)) {
          throw new AccessDeniedException(session.getUser(), arq);
      }  */
    }

    @Override
    public Arq getArq(ArqEnum arq) {
        return (Arq) hibernateTemplate.get(Arq.class, arq.name());
    }

    @Override
    public boolean isLoggedin() {
        return getUserState().isLoggedIn();
    }

    @Override
    public User getUser(UserState userState) {
        return null;
        // TODO
    }

    @Override
    public UserState getUserState() {
        if (session.getUserState() == null) {
            UserState userState = new UserState();
            userState.setSessionId(session.getSessionId());
            session.setUserState(userState);
            synchronized (userStates) {
                userStates.add(userState);
            }
            userGuidanceService.setLevelForNewUser(userState);
        }
        return session.getUserState();
    }

    @Override
    public UserState getUserState(DbBotConfig botConfig) {
        UserState userState = botStates.get(botConfig);
        if (userState == null) {
            userState = new UserState();
            userState.setBotConfig(botConfig);
            botStates.put(botConfig, userState);
        }
        return userState;
    }

    @Override
    public UserState getUserState(String sessionId) {
        synchronized (userStates) {
            for (UserState userState : userStates) {
                if (userState.getSessionId().equals(sessionId)) {
                    return userState;
                }
            }
        }
        throw new IllegalArgumentException("No user state for session id:" + sessionId + " The session was may closed");
    }

    @Override
    public SyncBaseObject getUserState(User user) {
        // TODO
        return null;
    }

    @Override
    public void onSessionTimedOut(UserState userState, String sessionId) {
        boolean hasBeenRemoved;
        synchronized (userStates) {
            hasBeenRemoved = userStates.remove(userState);
        }
        baseService.onSessionTimedOut(userState);
        if (hasBeenRemoved) {
            log.error("UserState could not be found for session: " + sessionId);
        }
    }

    @Override
    public List<UserState> getOnlineUserStates() {
        ArrayList<UserState> userStateCopy;
        synchronized (userStates) {
            userStateCopy = new ArrayList<UserState>(userStates);
        }
        return userStateCopy;
    }

    @Override
    public void restore(Collection<UserState> restoreUserStates) {
        botStates.clear();
        userStates.clear();
        for (UserState userState : restoreUserStates) {
            if (userState.isBot()) {
                botStates.put(userState.getBotConfig(), userState);
            } else {
                userStates.add(userState);
            }
        }
    }
}
