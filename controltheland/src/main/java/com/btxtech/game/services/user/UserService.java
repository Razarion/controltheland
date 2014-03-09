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

package com.btxtech.game.services.user;


import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.common.info.DetailedUser;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedNotVerifiedException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.common.NameErrorPair;
import com.btxtech.game.services.mgmt.RequestHelper;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.google.gwt.user.client.ui.SuggestOracle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {
    boolean login(String userName, String password) throws AlreadyLoggedInException;

    SimpleUser inGameLogin(String userName, String password) throws LoginFailedException, LoginFailedNotVerifiedException;

    boolean isRegistered();

    boolean isFacebookUserRegistered(FacebookSignedRequest facebookSignedRequest);

    void loginFacebookUser(FacebookSignedRequest facebookSignedRequest);

    boolean isFacebookLoggedIn(FacebookSignedRequest facebookSignedRequest);

    User getUser();

    String getUserName();

    String getUserName(UserState userState);

    SimpleUser getSimpleUser();

    User getUser(String name);

    User getUser(Integer userId);

    void logout();

    void inGameLogout();

    void save(User user);

    void updateLastNews(User user);

    List<User> getAllUsers();

    SuggestOracle.Response getSuggestedUserName(String nameQuery, UserNameSuggestionFilter filter, int limit);

    void createUser(String name, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, AlreadyLoggedInException;

    User createUnverifiedUser(String name, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, AlreadyLoggedInException, EmailAlreadyExitsException;

    void createAndLoginFacebookUser(FacebookSignedRequest facebookSignedRequest, String nickName) throws UserAlreadyExistsException, AlreadyLoggedInException;

    void setNewPassword(User user, String password);

    User getUser(UserState userState);

    UserState getUserState4Hash(int userStateHash);

    User getUser(SimpleBase simpleBase);

    Collection<GrantedAuthority> getAuthorities();

    boolean isAuthorized(String role);

    void checkAuthorized(String role);

    UserState getUserState();

    UserState getUserStateCms();

    boolean hasUserState();

    UserState getUserState(User user);

    void onSessionTimedOut(UserState userState);

    List<UserState> getAllUserStates();

    void restore(Collection<UserState> userStates);

    Collection<DbContentAccessControl> getDbContentAccessControls();

    Collection<DbPageAccessControl> getDbPageAccessControls();

    VerificationRequestCallback.ErrorResult isNickNameValid(String nickName);

    UserState createUserState(User user);

    RequestHelper getRequestHelper4ExceptionHandler();

    void loginIfNotLoggedIn(User userToLogin);

    void removeUserState(UserState userState);

    UserState getUserState(SimpleBase simpleBase);

    List<NameErrorPair> checkUserEmails(String usersAsString);

    List<User> getUsersWithEmail(String usersAsString);

    List<SimpleUser> getAllSimpleUsersWithLevel(int levelId);

    User loadUserFromDb(String name);

    User loadFacebookUserFromDb(String facebookUserId);

    DetailedUser createDetailedUser(User user);

    UserAttentionPacket createUserAttentionPacket();
}
