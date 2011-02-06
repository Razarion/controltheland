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

/**
 * User: beat
 * Date: 24.03.2010
 * Time: 11:46:43
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(User user, ArqEnum arq) {
        super("User '" + (user != null ? user.getName() : "<???>") + "' does not have ARQ: " +  arq.name());
    }
}
