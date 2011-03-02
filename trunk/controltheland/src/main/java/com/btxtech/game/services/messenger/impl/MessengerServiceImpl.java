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

package com.btxtech.game.services.messenger.impl;

import com.btxtech.game.services.messenger.InvalidFieldException;
import com.btxtech.game.services.messenger.Mail;
import com.btxtech.game.services.messenger.MessengerService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 03.04.2010
 * Time: 12:30:25
 */
@Component("messengerService")
public class MessengerServiceImpl implements MessengerService {
    private HibernateTemplate hibernateTemplate;
    @Autowired
    private UserService userService;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    @Secured(SecurityRoles.ROLE_USER)
    public int getUnreadMails() {
        final User user = userService.getUser();
        return hibernateTemplate.execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Mail.class);
                criteria.add(Restrictions.eq("user", user));
                criteria.add(Restrictions.eq("read", false));
                criteria.setProjection(Projections.rowCount());
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return (int) ((Number) criteria.list().get(0)).longValue();
            }
        });
    }

    @Override
    @Secured(SecurityRoles.ROLE_USER)
    public List<Mail> getMails() {
        final User user = userService.getUser();
        return (List<Mail>) hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Mail.class);
                criteria.add(Restrictions.eq("user", user));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.addOrder(Order.desc("sent"));
                return criteria.list();
            }
        });
    }

    @Override
    @Secured(SecurityRoles.ROLE_USER)
    public void sendMail(String to, String subject, String body) throws InvalidFieldException {
        final User fromUser = userService.getUser();
        if (to == null || to.isEmpty()) {
            throw new InvalidFieldException("To no allowed to be empty");
        }
        if (subject == null || subject.isEmpty()) {
            throw new InvalidFieldException("Subject no allowed to be empty");
        }
        if (body == null || body.isEmpty()) {
            throw new InvalidFieldException("Body no allowed to be empty");
        }

        List<User> users;
        if (to.equalsIgnoreCase(ALL_USERS)) {
            userService.checkAuthorized(SecurityRoles.ROLE_ADMINISTRATOR);
            users = userService.getAllUsers();
        } else {
            users = getUsers(to);
        }

        for (User toUser : users) {
            sendMail(toUser, to, fromUser, subject, body);
        }
    }

    private List<User> getUsers(String to) throws InvalidFieldException {
        ArrayList<User> users = new ArrayList<User>();
        StringTokenizer stringTokenizer = new StringTokenizer(to, TO_DELIMITER);
        while (stringTokenizer.hasMoreElements()) {
            String strUser = stringTokenizer.nextToken();
            strUser = strUser.trim();
            User user = userService.getUser(strUser);
            if (user == null) {
                throw new InvalidFieldException("Unknown user: " + strUser);
            }
            users.add(user);
        }
        return users;
    }

    private void sendMail(User to, String toString, User from, String subject, String body) {
        Mail mail = new Mail();
        mail.setSubject(subject);
        mail.setBody(body);
        mail.setFromUser(from.getUsername());
        mail.setRead(false);
        mail.setToUsers(toString);
        mail.setSent(new Date());
        mail.setUser(to);
        hibernateTemplate.save(mail);
    }

    @Override
    public void setMailRead(Mail mail) {
        if (mail.isRead()) {
            return;
        }
        mail.setRead(true);
        hibernateTemplate.update(mail);
    }
}
