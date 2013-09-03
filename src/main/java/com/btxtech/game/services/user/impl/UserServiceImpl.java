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

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.common.info.DetailedUser;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.info.Suggestion;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedNotVerifiedException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.NameErrorPair;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.NoBaseException;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.mgmt.RequestHelper;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.DbContentAccessControl;
import com.btxtech.game.services.user.DbGuildMember;
import com.btxtech.game.services.user.DbPageAccessControl;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.InvitationService;
import com.btxtech.game.services.user.NotAuthorizedException;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserNameSuggestionFilter;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.google.gwt.user.client.ui.SuggestOracle;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
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
import java.util.StringTokenizer;

@Component("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private com.btxtech.game.services.connection.Session session;
    @Autowired
    private RequestHelper requestHelper;
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
    @Autowired
    private ServerUnlockService serverUnlockService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private ServerGlobalConnectionService serverGlobalConnectionService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private InvitationService invitationService;
    @Value(value = "${security.md5salt}")
    private String md5HashSalt;
    private final Collection<UserState> userStates = new ArrayList<>();

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
            ExceptionHandler.handleException(authenticationException, "login failed: " + userName);
            return false;
        }
    }

    @Override
    public SimpleUser inGameLogin(String userName, String password) throws LoginFailedException, LoginFailedNotVerifiedException {
        if (getUserFromSecurityContext() != null) {
            throw new AlreadyLoggedInException(getUserFromSecurityContext());
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
            if (authentication.isAuthenticated()) {
                User user = (User) authentication.getPrincipal();
                loginUserFull(user, authentication);
                return user.createSimpleUser();
            } else {
                throw new LoginFailedException();
            }
        } catch (BadCredentialsException e) {
            throw new LoginFailedException();
        } catch (LockedException e) {
            throw new LoginFailedNotVerifiedException();
        } catch (AuthenticationException authenticationException) {
            ExceptionHandler.handleException(authenticationException, "login failed: " + userName);
            throw new LoginFailedException();
        }
    }

    @Override
    public void loginFacebookUser(FacebookSignedRequest facebookSignedRequest) {
        if (getUserFromSecurityContext() != null) {
            logout();
        }

        User user = loadFacebookUserFromDb(facebookSignedRequest.getUserId());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        loginUserFull(user, authentication);
    }

    private void setWicketSignedIn(boolean signedIn) {
        try {
            ((WicketAuthenticatedWebSession) AuthenticatedWebSession.get()).setSignIn(signedIn);
        } catch (Exception e) {
            // If coming from movable service, no wicket session available
            Object o = session.getRequest().getSession().getAttribute("wicket:wicket:" + org.apache.wicket.Session.SESSION_ATTRIBUTE_NAME);
            if (o == null) {
                throw new RuntimeException("Wicket session not found");
            }
            ((WicketAuthenticatedWebSession) o).setSignIn(signedIn);
        }
    }

    private void loginUserFull(User user, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        setWicketSignedIn(true);
        loginUser(user);
    }

    @Override
    public void inGameLogout() {
        logout();
        setWicketSignedIn(false);
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
            userState.setLocale(session.getRequest().getLocale());
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
        serverGlobalConnectionService.onLogout();
        if (session.getUserState() != null) {
            session.getUserState().setSessionId(null);
            session.setUserState(null);
        }
    }

    @Override
    public void loginIfNotLoggedIn(User userToLogin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            authentication = new UsernamePasswordAuthenticationToken(userToLogin, "", userToLogin.getAuthorities());
            loginUserFull(userToLogin, authentication);
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

    @Override
    public void removeUserState(UserState userState) {
        userState.setSessionId(null);
        planetSystemService.onUserStateRemoved(userState);
        if (!userState.isRegistered()) {
            synchronized (userStates) {
                userStates.remove(userState);
            }
            userGuidanceService.onRemoveUserState(userState);
            statisticsService.onRemoveUserState(userState);
            serverUnlockService.onUserStateRemoved(userState);
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
        user.registerUser(name, passwordHash, email, session.getDbInvitationInfo());
        privateSave(user);
        userTrackingService.onUserCreated(user);

        getUserState().setUser(user.getId());
        planetSystemService.onUserRegistered();
    }

    /**
     * This method creates a user and connect it to the current Base and UserState
     * The user must be verified during a specific time or the user will be deleted
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
    public User createUnverifiedUser(String name, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, AlreadyLoggedInException, EmailAlreadyExitsException {
        if (getUserFromSecurityContext() != null) {
            throw new AlreadyLoggedInException(getUserFromSecurityContext());
        }

        if (getUser(name) != null) {
            throw new UserAlreadyExistsException();
        }

        if (!password.equals(confirmPassword)) {
            throw new PasswordNotMatchException();
        }

        checkExits(email);

        User user = new User();
        String passwordHash = new Md5PasswordEncoder().encodePassword(password, md5HashSalt);
        user.registerUser(name, passwordHash, email, session.getDbInvitationInfo());
        user.setAwaitingVerification();
        privateSave(user);
        userTrackingService.onUserCreated(user);

        getUserState().setUser(user.getId());
        planetSystemService.onUserRegistered();

        return user;
    }

    @Override
    public void setNewPassword(User user, String password) {
        String passwordHash = new Md5PasswordEncoder().encodePassword(password, md5HashSalt);
        user.setPassword(passwordHash);
        privateSave(user);
    }

    @SuppressWarnings("unchecked")
    private void checkExits(String email) throws EmailAlreadyExitsException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.eq("email", email));
        List<User> users = criteria.list();
        if (users != null && !users.isEmpty()) {
            throw new EmailAlreadyExitsException(email);
        }
    }

    @Override
    public void createAndLoginFacebookUser(FacebookSignedRequest facebookSignedRequest, String nickName) throws UserAlreadyExistsException, AlreadyLoggedInException {
        if (getUserFromSecurityContext() != null) {
            logout();
        }

        if (getUser(nickName) != null) {
            throw new UserAlreadyExistsException();
        }

        User user = new User();
        user.registerFacebookUser(facebookSignedRequest, nickName, session.getDbFacebookSource(), session.getDbInvitationInfo());
        privateSave(user);
        invitationService.onUserRegisteredAndVerified(user);
        userTrackingService.onUserCreated(user);

        getUserState().setUser(user.getId());
        planetSystemService.onUserRegistered();

        loginFacebookUser(facebookSignedRequest);
    }

    @Override
    public VerificationRequestCallback.ErrorResult isNickNameValid(String nickName) {
        if (nickName == null || nickName.isEmpty()) {
            return VerificationRequestCallback.ErrorResult.TO_SHORT;
        }

        if (nickName.length() < 3) {
            return VerificationRequestCallback.ErrorResult.TO_SHORT;
        }
        if (!isNicknameFree(nickName)) {
            return VerificationRequestCallback.ErrorResult.ALREADY_USED;
        }

        return null;
    }

    @Override
    public boolean isRegistered() {
        return hasUserState() && getUserState().isRegistered() && getUser().isRegistrationComplete();
    }

    @Override
    public boolean isFacebookUserRegistered(FacebookSignedRequest facebookUserId) {
        return loadFacebookUserFromDb(facebookUserId.getUserId()) != null;
    }

    @Override
    public User getUser() {
        UserState userState = getUserState();
        if (userState.isRegistered()) {
            return loadUserFromDb(userState.getUser());
        } else {
            return null;
        }
    }

    @Override
    public String getUserName() {
        User user = getUser();
        if (user != null) {
            return user.getUsername();
        } else {
            return null;
        }
    }

    @Override
    public String getUserName(UserState userState) {
        User user = getUser(userState);
        if (user != null) {
            return user.getUsername();
        } else {
            return null;
        }
    }

    @Override
    public SimpleUser getSimpleUser() {
        User user = getUser();
        if (user != null) {
            return user.createSimpleUser();
        } else {
            return null;
        }
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
    public User getUser(Integer userId) {
        if (userId == null) {
            return null;
        }
        return loadUserFromDb(userId);
    }

    @Override
    public User getUser(SimpleBase simpleBase) {
        if (simpleBase == null) {
            return null;
        }
        UserState userState = getUserState(simpleBase);
        if (userState == null) {
            return null;
        }
        return getUser(userState);
    }

    @Override
    public UserState getUserState(SimpleBase simpleBase) {
        if (simpleBase == null) {
            return null;
        }
        return planetSystemService.getServerPlanetServices(simpleBase).getBaseService().getUserState(simpleBase);
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
        Hibernate.initialize(user.getContentCrud().readDbChildren());
        for (DbContentAccessControl dbContentAccessControl : user.getContentCrud().readDbChildren()) {
            Hibernate.initialize(dbContentAccessControl.getDbContent());
        }
        Hibernate.initialize(user.getPageCrud().readDbChildren());
        for (DbPageAccessControl dbPageAccessControl : user.getPageCrud().readDbChildren()) {
            Hibernate.initialize(dbPageAccessControl.getDbPage());
        }
    }

    @Override
    public User loadUserFromDb(String name) {
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

    private User loadUserFromDb(int userId) {
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            return loadUserFromDbInSession(userId);
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                return loadUserFromDbInSession(userId);
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

    @SuppressWarnings("unchecked")
    private User loadUserFromDbInSession(int userId) {
        // Get user from DB
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.eq("id", userId));
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

    private User getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((User) authentication.getPrincipal());
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

    @Override
    @Transactional
    public void updateLastNews(User user) {
        user.updateLastNews();
        save(user);
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
    public SuggestOracle.Response getSuggestedUserName(String nameQuery, UserNameSuggestionFilter filter, int limit) {
        if (StringUtils.isEmpty(nameQuery)) {
            return new SuggestOracle.Response();
        }
        nameQuery = nameQuery.toUpperCase();
        Criteria criteria = createUserSuggestionCriteria(nameQuery, filter, true);
        criteria.setMaxResults(limit);
        List<String> usersNames = criteria.list();
        if (usersNames != null) {
            Criteria countCriteria = createUserSuggestionCriteria(nameQuery, filter, false);
            countCriteria.setProjection(Projections.rowCount());
            int remaining = Math.max(0, ((Number) countCriteria.uniqueResult()).intValue() - usersNames.size());
            SuggestOracle.Response response = new SuggestOracle.Response(Suggestion.createSuggestionCollection(usersNames));
            response.setMoreSuggestionsCount(remaining);
            return response;
        } else {
            return new SuggestOracle.Response();
        }
    }

    private Criteria createUserSuggestionCriteria(String nameQuery, UserNameSuggestionFilter filter, boolean order) {
        switch (filter) {
            case USER_GILD_SEARCH: {
                Criteria guildMemberCriteria = sessionFactory.getCurrentSession().createCriteria(DbGuildMember.class);
                guildMemberCriteria.add(Restrictions.isNull("id"));
                Criteria userCriteria = guildMemberCriteria.createCriteria("user", "userAlias", JoinType.RIGHT_OUTER_JOIN);
                userCriteria.add(Restrictions.ilike("name", nameQuery + "%"));
                userCriteria.add(Restrictions.eq("accountNonLocked", true));
                userCriteria.setProjection(Projections.property("userAlias.name"));
                if (order) {
                    userCriteria.addOrder(Order.asc("name"));
                }
                return guildMemberCriteria;
            }
            case USER_TRACKING_SEARCH: {
                Criteria userCriteria = sessionFactory.getCurrentSession().createCriteria(User.class);
                userCriteria.add(Restrictions.ilike("name", nameQuery + "%"));
                userCriteria.add(Restrictions.eq("accountNonLocked", true));
                userCriteria.setProjection(Projections.property("name"));
                if (order) {
                    userCriteria.addOrder(Order.asc("name"));
                }
                return userCriteria;
            }
            default:
                throw new IllegalArgumentException("Unknown filter: " + filter);
        }
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
            userState.setLocale(session.getRequest().getLocale());
            session.setUserState(userState);
        }
        return session.getUserState();
    }

    @Override
    public UserState createUserState(User user) {
        if (user != null) {
            synchronized (userStates) {
                for (UserState userState : userStates) {
                    if (userState.getUser() != null && userState.getUser().equals(user.getId())) {
                        throw new IllegalArgumentException("User already has a user state: " + user);
                    }
                }
            }
        }
        UserState userState = new UserState();
        synchronized (userStates) {
            userStates.add(userState);
        }
        serverUnlockService.onUserStateCreated(userState);
        userGuidanceService.setLevelForNewUser(userState);
        globalInventoryService.setupNewUserState(userState);
        if (user != null) {
            userState.setUser(user.getId());
        }
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
                if (user.getId().equals(userState.getUser())) {
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

    @Override
    public RequestHelper getRequestHelper4ExceptionHandler() {
        try {
            // To figure out if session is active
            requestHelper.getRequest();
            return requestHelper;
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public List<NameErrorPair> checkUserEmails(String usersAsString) {
        List<NameErrorPair> result = new ArrayList<>();
        if (StringUtils.isBlank(usersAsString)) {
            return result;
        }
        List<String> userNames = getUserNameList(usersAsString);
        for (String userName : userNames) {
            User user = loadUserFromDb(userName);
            if (user == null) {
                result.add(new NameErrorPair(userName, "Nu such user"));
            } else if (user.getEmail() == null) {
                result.add(new NameErrorPair(userName, "No email address"));
            }
        }
        return result;
    }

    private List<String> getUserNameList(String usersAsString) {
        StringTokenizer tokenizer = new StringTokenizer(usersAsString);
        List<String> userNames = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            userNames.add(tokenizer.nextToken());
        }
        return userNames;
    }

    @Override
    public List<User> getUsersWithEmail(String usersAsString) {
        List<User> result = new ArrayList<>();
        if (StringUtils.isBlank(usersAsString)) {
            return result;
        }
        List<String> userNames = getUserNameList(usersAsString);
        for (String userName : userNames) {
            User user = loadUserFromDb(userName);
            if (user != null && user.getEmail() != null) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public DetailedUser createDetailedUser(User user) {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(user);
        return new DetailedUser(user.createSimpleUser(),
                userGuidanceService.getDbLevel(user).getNumber(),
                serverPlanetServices != null ? serverPlanetServices.getPlanetInfo().getName() : null);
    }

    @Override
    public UserAttentionPacket createUserAttentionPacket() {
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        User user = getUser();
        if (user != null) {
            contentService.fillUserAttentionPacket(user, userAttentionPacket);
            guildService.fillUserAttentionPacket(user, userAttentionPacket);
        }
        return userAttentionPacket;
    }


}
