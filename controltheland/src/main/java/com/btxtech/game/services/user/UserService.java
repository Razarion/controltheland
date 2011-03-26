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


import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.bot.DbBotConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {

    boolean login(String userName, String password);

    boolean isRegistered();

    User getUser();

    void logout();

    User getUser(String name);

    void save(User user);

    List<User> getAllUsers();

    void createUser(String name, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException;

    User getUser(UserState userState);

    UserState getUserState4Hash(int userStateHash);

    User getUser(SimpleBase simpleBase);

    Collection<GrantedAuthority> getAuthorities();

    boolean isAuthorized(String role);

    void checkAuthorized(String role);

    UserState getUserState();

    UserState getUserState(User user);

    UserState getUserState(DbBotConfig botConfig);

    void onSessionTimedOut(UserState userState, String sessionId);

    List<UserState> getAllUserStates();

    void restore(Collection<UserState> userStates);

    void onSurrenderBase();
}
