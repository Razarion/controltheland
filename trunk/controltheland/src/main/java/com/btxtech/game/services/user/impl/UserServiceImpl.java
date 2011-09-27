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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.DbContentAccessControl;
import com.btxtech.game.services.user.DbPageAccessControl;
import com.btxtech.game.services.user.NotAuthorizedException;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private com.btxtech.game.services.connection.Session session;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Value(value = "${security.md5salt}")
    private String md5HashSalt;

    private HibernateTemplate hibernateTemplate;
    private final Map<DbBotConfig, UserState> botStates = new HashMap<DbBotConfig, UserState>();
    private final Collection<UserState> userStates = new ArrayList<UserState>();
    private Log log = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public boolean login(String userName, String password) throws AlreadyLoggedInException {
        if (getUser() != null) {
            throw new AlreadyLoggedInException(getUser());
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (authentication.isAuthenticated()) {
                loginUser((User) authentication.getPrincipal());
                return true;
            } else {
                return false;
            }
        } catch (AuthenticationException authenticationException) {
            log.error("", authenticationException);
            return false;
        }
    }

    private void loginUser(User user) {
        user.setLastLoginDate(new Date());
        privateSave(user);
        UserState userState = getUserState(user);
        if (userState != null) {
            userState.setSessionId(session.getSessionId());
        }
        session.setUserState(userState);
        try {
            userTrackingService.onUserLoggedIn(user, baseService.getBase());
        } catch (NoConnectionException e) {
            // Ignore
            userTrackingService.onUserLoggedIn(user, null);
        }
    }

    @Override
    public void logout() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(null);
        if (user != null) {
            userTrackingService.onUserLoggedOut(user);
        }
        if (session.getUserState() != null) {
            session.getUserState().setSessionId(null);
            session.setUserState(null);
        }
    }

    @Override
    public void onSessionTimedOut(UserState userState, String sessionId) {
        if (userState != null) {
            userState.setSessionId(null);
            baseService.onSessionTimedOut(userState);
            if (!userState.isRegistered()) {
                synchronized (userStates) {
                    userStates.remove(userState);
                }
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                userTrackingService.onUserLoggedOut(user);
            }
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    /**
     * This method creates a user and connect it to the current Base and UserState
     * <p/>
     * After this method login must be called immediately!     *
     *
     * @param name            User name
     * @param password        password
     * @param confirmPassword confirm password
     * @param email           email
     * @throws UserAlreadyExistsException
     * @throws PasswordNotMatchException
     */
    @Override
    public void createUser(String name, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, AlreadyLoggedInException {
        if (getUser() != null) {
            throw new AlreadyLoggedInException(getUser());
        }

        if (getUser(name) != null) {
            throw new UserAlreadyExistsException();
        }

        if (!password.equals(confirmPassword)) {
            throw new PasswordNotMatchException();
        }

        User user = new User();
        String passwordHash = new Md5PasswordEncoder().encodePassword(password, md5HashSalt);
        user.registerUser(name, passwordHash, email);
        privateSave(user);
        userTrackingService.onUserCreated(user);

        getUserState().setUser(user);
        baseService.onUserRegistered();
    }

    @Override
    public boolean isRegistered() {
        return hasUserState() && getUserState().isRegistered();
    }

    @Override
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        return getUser(username);
    }

    @Override
    public User getUser(final String name) {
        List<User> users = hibernateTemplate.execute(new HibernateCallback<List<User>>() {
            @SuppressWarnings("unchecked")
            public List<User> doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(User.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("name", name));
                return criteria.list();
            }
        });
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            User user = users.get(0);
            Hibernate.initialize(user.getContentCrud().readDbChildren());
            for (DbContentAccessControl dbContentAccessControl : user.getContentCrud().readDbChildren()) {
                Hibernate.initialize(dbContentAccessControl.getDbContent());
            }
            Hibernate.initialize(user.getPageCrud().readDbChildren());
            for (DbPageAccessControl dbPageAccessControl : user.getPageCrud().readDbChildren()) {
                Hibernate.initialize(dbPageAccessControl.getDbPage());
            }
            return user;
        }
    }

    @Override
    public User getUser(UserState userState) {
        return userState.getUser();
    }

    @Override
    public UserState getUserState4Hash(int userStateHash) {
        for (UserState userState : userStates) {
            if (userState.hashCode() == userStateHash) {
                return userState;
            }
        }
        throw new IllegalArgumentException("No UserState for hash: " + userStateHash);
    }

    @Override
    public User getUser(SimpleBase simpleBase) {
        UserState userState = baseService.getUserState(simpleBase);
        if (userState == null) {
            return null;
        }
        return userState.getUser();
    }

    @Override
    @Transactional
    public void save(User user) {
        hibernateTemplate.saveOrUpdate(user);
    }

    private void privateSave(final User user) {
        // @Transactional not working here
        // It's an limitation with Spring AOP. (dynamic objects and CGLIB)
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                hibernateTemplate.saveOrUpdate(user);
            }
        });
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
    public Collection<GrantedAuthority> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }
        return authentication.getAuthorities();
    }

    @Override
    public boolean isAuthorized(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkAuthorized(String role) {
        if (!isAuthorized(role)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public UserState getUserState() {
        if (session.getUserState() == null) {
            UserState userState = new UserState();
            userState.setSessionId(session.getSessionId());
            userState.setUser(getUser());
            session.setUserState(userState);
            synchronized (userStates) {
                userStates.add(userState);
            }
            userGuidanceService.setLevelForNewUser(userState);
        }
        return session.getUserState();
    }

    private boolean hasUserState() {
        return session.getUserState() != null;
    }

    @Override
    public UserState getUserState(User user) {
        synchronized (userStates) {
            for (UserState userState : userStates) {
                if (user.equals(userState.getUser())) {
                    return userState;
                }
            }
        }
        return null;
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
    public void deleteUserState(DbBotConfig botConfig) {
        botStates.remove(botConfig);
    }

    @Override
    public List<UserState> getAllUserStates() {
        ArrayList<UserState> userStateCopy;
        synchronized (userStates) {
            userStateCopy = new ArrayList<UserState>(userStates);
        }
        return userStateCopy;
    }

    @Override
    public List<UserState> getAllBotUserStates() {
        ArrayList<UserState> userStateCopy;
        synchronized (botStates) {
            userStateCopy = new ArrayList<UserState>(botStates.values());
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

    @Override
    public Collection<DbContentAccessControl> getDbContentAccessControls() {
        User user = getUser();
        if (user == null) {
            return null;
        }
        Collection<DbContentAccessControl> dbContentAccessControls = user.getContentCrud().readDbChildren();
        if (dbContentAccessControls == null || dbContentAccessControls.isEmpty()) {
            return null;
        }
        return dbContentAccessControls;
    }

    @Override
    public Collection<DbPageAccessControl> getDbPageAccessControls() {
        User user = getUser();
        if (user == null) {
            return null;
        }
        Collection<DbPageAccessControl> dbPageAccessControls = user.getPageCrud().readDbChildren();
        if (dbPageAccessControls == null || dbPageAccessControls.isEmpty()) {
            return null;
        }
        return dbPageAccessControls;
    }

}