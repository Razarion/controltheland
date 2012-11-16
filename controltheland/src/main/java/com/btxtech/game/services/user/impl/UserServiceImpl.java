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
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.NoBaseException;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.DbContentAccessControl;
import com.btxtech.game.services.user.DbPageAccessControl;
import com.btxtech.game.services.user.InvalidNickName;
import com.btxtech.game.services.user.NotAuthorizedException;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
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
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Value(value = "${security.md5salt}")
    private String md5HashSalt;

    private final Collection<UserState> userStates = new ArrayList<>();
    private Log log = LogFactory.getLog(UserServiceImpl.class);

    public UserServiceImpl() {
        ExceptionHandler.init(this);
    }

    @Override
    public boolean login(String userName, String password) throws AlreadyLoggedInException {
        if (getUserFromSecurityContext() != null) {
            throw new AlreadyLoggedInException(getUserFromSecurityContext());
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

    @Override
    public void loginFacebookUser(FacebookSignedRequest facebookSignedRequest) {
        User user = loadFacebookUserFromDb(facebookSignedRequest.getUserId());

        if (getUserFromSecurityContext() != null) {
            throw new AlreadyLoggedInException(getUserFromSecurityContext());
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ((WicketAuthenticatedWebSession) AuthenticatedWebSession.get()).setSignIn();
        loginUser(user);
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
            userTrackingService.onUserLoggedIn(user, userState);
        } catch (NoBaseException e) {
            // Ignore
            userTrackingService.onUserLoggedIn(user, userState);
        }
    }

    @Override
    public boolean isFacebookLoggedIn(FacebookSignedRequest facebookSignedRequest) {
        User user = getUser();
        return user != null
                && user.getSocialNet() == User.SocialNet.FACEBOOK
                && user.getSocialNetUserId().equals(facebookSignedRequest.getUserId());
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
    public void onSessionTimedOut(UserState userState) {
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
        planetSystemService.onSessionTimedOut(userState);
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
        if (getUserFromSecurityContext() != null) {
            throw new AlreadyLoggedInException(getUserFromSecurityContext());
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

        getUserState().setUser(name);
        planetSystemService.onUserRegistered();
    }

    @Override
    public void createAndLoginFacebookUser(FacebookSignedRequest facebookSignedRequest, String nickName) throws UserAlreadyExistsException, AlreadyLoggedInException {
        if (getUserFromSecurityContext() != null) {
            throw new AlreadyLoggedInException(getUserFromSecurityContext());
        }

        if (getUser(facebookSignedRequest.getUserId()) != null) {
            throw new UserAlreadyExistsException();
        }

        User user = new User();
        user.registerFacebookUser(facebookSignedRequest, nickName);
        privateSave(user);
        userTrackingService.onUserCreated(user);

        getUserState().setUser(nickName);
        planetSystemService.onUserRegistered();

        loginFacebookUser(facebookSignedRequest);
    }

    @Override
    public InvalidNickName isNickNameValid(String nickName) {
        if (nickName == null || nickName.isEmpty()) {
            return InvalidNickName.TO_SHORT;
        }

        if (nickName.length() < 3) {
            return InvalidNickName.TO_SHORT;
        }
        if (!isNicknameFree(nickName)) {
            return InvalidNickName.ALREADY_USED;
        }

        return null;
    }

    @Override
    public boolean isRegistered() {
        return hasUserState() && getUserState().isRegistered();
    }

    @Override
    public boolean isFacebookUserRegistered(FacebookSignedRequest facebookUserId) {
        return loadFacebookUserFromDb(facebookUserId.getUserId()) != null;
    }

    @Override
    public User getUser() {
        String userName = getUserName();
        if (userName == null) {
            return null;
        }
        return loadUserFromDb(userName);
    }

    @Override
    public String getUserName() {
        return getUserState().getUser();
    }

    @Override
    public User getUser(UserState userState) {
        if (!userState.isRegistered()) {
            return null;
        }
        return loadUserFromDb(userState.getUser());
    }

    @Override
    public User getUser(String name) {
        return loadUserFromDb(name);
    }

    @Override
    public User getUser(SimpleBase simpleBase) {
        if(simpleBase == null) {
            return null;
        }
        UserState userState = planetSystemService.getServerPlanetServices(simpleBase).getBaseService().getUserState(simpleBase);
        if (userState == null) {
            return null;
        }
        return getUser(userState);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        User user = loadUserFromDb(username);
        if (user != null) {
            loadDependencies(user);
            return user;
        } else {
            throw new UsernameNotFoundException("User does not exist: " + username);
        }
    }

    private void loadDependencies(User user) {
        Hibernate.initialize(user.getAlliances());
        Hibernate.initialize(user.getAllianceOffers());
        Hibernate.initialize(user.getContentCrud().readDbChildren());
        for (DbContentAccessControl dbContentAccessControl : user.getContentCrud().readDbChildren()) {
            Hibernate.initialize(dbContentAccessControl.getDbContent());
        }
        Hibernate.initialize(user.getPageCrud().readDbChildren());
        for (DbPageAccessControl dbPageAccessControl : user.getPageCrud().readDbChildren()) {
            Hibernate.initialize(dbPageAccessControl.getDbPage());
        }
    }

    private User loadUserFromDb(String name) {
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            return loadUserFromDbInSession(name);
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                return loadUserFromDbInSession(name);
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private User loadFacebookUserFromDb(String facebookUserId) {
        // Get user from DB
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.eq("socialNet", User.SocialNet.FACEBOOK));
        criteria.add(Restrictions.eq("socialNetUserId", facebookUserId));
        List<User> users = criteria.list();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    private User loadUserFromDbInSession(String name) {
        // Get user from DB
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.eq("name", name));
        List<User> users = criteria.list();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    private boolean isNicknameFree(String nickName) {
        // Get user from DB
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setProjection(Projections.rowCount());
        criteria.add(Restrictions.eq("name", nickName));
        return ((Number) criteria.list().get(0)).intValue() == 0;
    }

    private String getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((User) authentication.getPrincipal()).getUsername();
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
                save(user);
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
            UserState userState = createUserState(getUserFromSecurityContext());
            userState.setSessionId(session.getSessionId());
            session.setUserState(userState);
        }
        return session.getUserState();
    }

    @Override
    public UserState createUserState(String userName) {
        UserState userState = new UserState();
        synchronized (userStates) {
            userStates.add(userState);
        }
        userGuidanceService.setLevelForNewUser(userState);
        globalInventoryService.setupNewUserState(userState);
        userState.setUser(userName);
        return userState;
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
                if (user.getUsername().equals(userState.getUser())) {
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
            userStateCopy = new ArrayList<>(userStates);
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
