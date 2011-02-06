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

package com.btxtech.game.services.messenger;

import java.util.List;

/**
 * User: beat
 * Date: 31.03.2010
 * Time: 22:25:00
 */
public interface MessengerService {
    static final String ALL_USERS = "<ALL>";
    static final String TO_DELIMITER = ";";

    int getUnreadMails();

    List<Mail> getMails();

    void sendMail(String to, String subject, String body) throws InvalidFieldException;

    void setMailRead(Mail mail);
}
