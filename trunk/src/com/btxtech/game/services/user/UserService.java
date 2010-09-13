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


import java.util.List;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;

public interface UserService {

    boolean login(String name, String password);

    boolean isLoggedin();

    User getLoggedinUser();

    User getLoggedinUserOrException();

    void logout();

    User getUser(String name);

    void save(User user);

    List<User> getAllUsers();

    void createUserAndLoggin(String name, String password, String confirmPassword, String email, boolean keepGame) throws UserAlreadyExistsException, PasswordNotMatchException;

    boolean isAuthorized(ArqEnum arq);

    void checkAuthorized(ArqEnum arq);

    Arq getArq(ArqEnum arq);
}
