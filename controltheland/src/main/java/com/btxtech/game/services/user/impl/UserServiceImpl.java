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
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.statistics.StatisticsService;
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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
import java.util.List;

@Component("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private com.btxtech.game.services.connection.Session session;
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
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private StatisticsService statisticsService;
    @Value(value = "${security.md5salt}")
    private String md5HashSalt;

    private final Collection<UserState> userStates = new ArrayList<UserState>();
    private Log log = LogFactory.getLog(UserServiceImpl.class);

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
        } catch (BadCredentialsException e) {
            return false;
        } catch (AuthenticationException authenticationException) {
            log.error("", authenticationException);
            return false;
        }
    }

    private void loginUser(User user) {
        user.setLastLoginDate(new Date());
        privateSave(user);
        if (hasUserState()) {
            //handle old userState
            removeUserState(getUserState());
        }
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
            removeUserState(userState);
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

    private void removeUserState(UserState userState) {
        userState.setSessionId(null);
        baseService.onSessionTimedOut(userState);
        if (!userState.isRegistered()) {
            synchronized (userStates) {
                userStates.remove(userState);
            }
            userGuidanceService.onRemoveUserState(userState);
            statisticsService.onRemoveUserState(userState);
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
        User user = getUser(username);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("User does not exist: " + username);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getUser(final String name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.eq("name", name));
        List<User> users = criteria.list();
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
        sessionFactory.getCurrentSession().saveOrUpdate(user);
    }

    private void privateSave(final User user) {
        // @Transactional not working here
        // It's an limitation with Spring AOP. (dynamic objects and CGLIB)
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                sessionFactory.getCurrentSession().saveOrUpdate(user);
            }
        });
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAllUsers() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (List<User>) criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }
        return (Collection<GrantedAuthority>) authentication.getAuthorities();
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

    @Override
    public UserState getUserStateCms() {
        // Prevent creating a UserState -> search engine
        if (hasUserState()) {
            return getUserState();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasUserState() {
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
    public List<UserState> getAllUserStates() {
        ArrayList<UserState> userStateCopy;
        synchronized (userStates) {
            userStateCopy = new ArrayList<UserState>(userStates);
        }
        return userStateCopy;
    }

    @Override
    public void restore(Collection<UserState> restoreUserStates) {
        userStates.clear();
        for (UserState userState : restoreUserStates) {
            userStates.add(userState);
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
