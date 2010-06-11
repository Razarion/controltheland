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
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.user.Arq;
import com.btxtech.game.services.user.ArqEnum;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.AccessDeniedException;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.Date;
import java.util.List;
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
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public boolean login(String name, String password) {
        if (session.getUser() != null) {
            throw new IllegalStateException("The user is already logged in: " + session.getUser());
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
        }
    }

    @Override
    public boolean isLoggedin() {
        return session.getUser() != null;
    }

    @Override
    public User getLoggedinUser() {
        return session.getUser();
    }

    @Override
    public User getLoggedinUserOrException() {
        User user = getLoggedinUser();
        if(user == null) {
           throw new RuntimeException("User is not logged in");
        }
        return user;
    }

    @Override
    public void logout() {
        if (session.getUser() == null) {
            throw new IllegalStateException("The user is not logged in");
        }
        session.clearGame();
        serverMarketService.clearSession();
        userTrackingService.onUserLoggedOut(session.getUser());
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
    public void createUserAndLoggin(String name, String password, String confirmPassword) throws UserAlreadyExistsException, PasswordNotMatchException {
        if (getUser(name) != null) {
            throw new UserAlreadyExistsException();
        }

        if (!password.equals(confirmPassword)) {
            throw new PasswordNotMatchException();
        }
        User user = createAndSaveUser(name, password);
        loginUser(user, true);
    }

    @Override
    public boolean isAuthorized(ArqEnum arq) {
        User user = session.getUser();
        return user != null && user.hasArq(getArq(arq));
    }

    @Override
    public void checkAuthorized(ArqEnum arq) {
        if(!isAuthorized(arq)) {
           throw new AccessDeniedException(session.getUser(), arq);  
        }
    }

    @Override
    public Arq getArq(ArqEnum arq) {
        return (Arq) hibernateTemplate.get(Arq.class, arq.name());
    }

    private User createAndSaveUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setRegisterDate(new Date());
        user.addArq(getArq(ArqEnum.FORUM_POST));
        save(user);
        userTrackingService.onUserCreated(user);
        return user;
    }

    private void loginUser(User user, boolean keepGame) {
        Base base = null;
        if (keepGame) {
            try {
                base = baseService.getBase();
                base.setUser(user);
                serverMarketService.setUserItemTypeAccess(user, serverMarketService.getUserItemTypeAccess());
            } catch (NoConnectionException e) {
                // Ignore
            }
        } else {
            session.clearGame();
            serverMarketService.clearSession();
        }
        session.setUser(user);
        user.setLastLoginDate(new Date());
        save(user);
        userTrackingService.onUserLoggedIn(user, base);
    }

}
