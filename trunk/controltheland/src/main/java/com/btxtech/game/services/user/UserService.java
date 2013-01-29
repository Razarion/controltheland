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


import com.btxtech.game.jsre.client.AdCellProvision;
import com.btxtech.game.jsre.client.InvalidNickName;
import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {
    boolean login(String userName, String password) throws AlreadyLoggedInException;

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

    void save(User user);

    List<User> getAllUsers();

    void createUser(String name, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, AlreadyLoggedInException;

    User createUnverifiedUser(String name, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, AlreadyLoggedInException, EmailAlreadyExitsException;

    void createAndLoginFacebookUser(FacebookSignedRequest facebookSignedRequest, String nickName) throws UserAlreadyExistsException, AlreadyLoggedInException;

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

    InvalidNickName isNickNameValid(String nickName);

    UserState createUserState(User user);

    Session getSession4ExceptionHandler();

    void loginIfNotLoggedIn(User userToLogin);

    void removeUserState(UserState userState);

    UserState getUserState(SimpleBase simpleBase);

    AdCellProvision handleAdCellProvision();
}
