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

package com.btxtech.game.services.common;

import com.btxtech.game.services.user.UserService;

import java.io.Serializable;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:24:13
 */
public interface CrudChild<T> {
    String getName();

    void setName(String name);

    void init(UserService userService);

    void setParent(T t);

    T getParent();

    Serializable getId();
}
