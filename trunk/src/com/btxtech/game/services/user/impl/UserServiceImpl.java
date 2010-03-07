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
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
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
    private ServerItemTypeAccessService serverItemTypeAccessService;
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
    public void logout() {
        if (session.getUser() == null) {
            throw new IllegalStateException("The user is not logged in");
        }
        session.clearGame();
        serverItemTypeAccessService.clearSession();
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

    private User createAndSaveUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setRegisterDate(new Date());
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
                base.setUserItemTypeAccess(serverItemTypeAccessService.getUserItemTypeAccess());
            } catch (NoConnectionException e) {
                // Ignore
            }
        } else {
            session.clearGame();
            serverItemTypeAccessService.clearSession();
        }
        session.setUser(user);
        user.setLastLoginDate(new Date());
        save(user);
        userTrackingService.onUserLoggedIn(user, base);
    }

}
