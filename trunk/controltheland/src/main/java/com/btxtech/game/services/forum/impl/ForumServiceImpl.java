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

package com.btxtech.game.services.forum.impl;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.forum.AbstractForumEntry;
import com.btxtech.game.services.forum.Category;
import com.btxtech.game.services.forum.ForumService;
import com.btxtech.game.services.forum.ForumThread;
import com.btxtech.game.services.forum.Post;
import com.btxtech.game.services.forum.SubForum;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:05:26
 */
@Component("forumService")
public class ForumServiceImpl implements ForumService {
    private Log log = LogFactory.getLog(ForumServiceImpl.class);
    private HibernateTemplate hibernateTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private CrudRootServiceHelper<SubForum> subForumCrud;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        subForumCrud.init(SubForum.class, null, false, false, "user");
    }

    @Override
    public CrudRootServiceHelper<SubForum> getSubForumCrud() {
        return subForumCrud;
    }

    @Override
    @Deprecated
    public List<SubForum> getSubForums() {
        return hibernateTemplate.execute(new HibernateCallback<List<SubForum>>() {
            @Override
            public List<SubForum> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(SubForum.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    public Category getCategory(int categoryId) {
        return hibernateTemplate.get(Category.class, categoryId);
    }

    @Override
    public List<Category> getCategories(final SubForum subForum) {
        List<Category> categories = hibernateTemplate.execute(new HibernateCallback<List<Category>>() {
            @Override
            public List<Category> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Category.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("subForum", subForum));
                return criteria.list();
            }
        });
        for (Category category : categories) {
            category.setLastPost(getLatestPost(category));
        }
        Collections.sort(categories, new LastPostComparator());
        return categories;
    }

    @Override
    public ForumThread getForumThread(int forumThreadId) {
        return hibernateTemplate.get(ForumThread.class, forumThreadId);
    }

    @Override
    public List<ForumThread> getForumThreads(final Category category) {
        List<ForumThread> forumThreads = hibernateTemplate.execute(new HibernateCallback<List<ForumThread>>() {
            @Override
            public List<ForumThread> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ForumThread.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("category", category));
                return criteria.list();
            }
        });
        for (ForumThread forumThread : forumThreads) {
            forumThread.setLastPost(getLatestPost(forumThread));
        }

        Collections.sort(forumThreads, new LastPostComparator());
        return forumThreads;
    }

    @Override
    public List<Post> getPosts(final ForumThread forumThread) {
        return hibernateTemplate.execute(new HibernateCallback<List<Post>>() {
            @Override
            public List<Post> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Post.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("forumThread", forumThread));
                criteria.addOrder(Order.asc("date"));
                return criteria.list();
            }
        });
    }

    @Override
    public AbstractForumEntry createForumEntry(Class<? extends AbstractForumEntry> aClass) {
        try {
            return aClass.getConstructor().newInstance();
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    @Secured(SecurityRoles.ROLE_FORUM_ADMINISTRATOR)
    @Transactional
    public void insertSubForumEntry(final SubForum subForum) {
        subForum.setUser(userService.getUser());
        subForum.setDate();
        hibernateTemplate.save(subForum);
    }

    @Override
    @Secured(SecurityRoles.ROLE_FORUM_ADMINISTRATOR)
    @Transactional
    public void insertCategoryEntry(final Serializable parentId, final Category category) {
        category.setUser(userService.getUser());
        hibernateTemplate.execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(SubForum.class);
                criteria.add(Restrictions.eq("id", parentId));
                List<SubForum> subForums = (List<SubForum>) criteria.list();
                if (subForums == null || subForums.isEmpty()) {
                    throw new IllegalArgumentException("SubForum not found: " + parentId);
                }
                SubForum subForum = subForums.get(0);
                subForum.addCategory(category);
                session.saveOrUpdate(subForum);
                return null;
            }
        });
    }

    @Override
    @Secured(SecurityRoles.ROLE_USER)
    @Transactional
    public void insertForumThreadEntry(final Serializable parentId, final ForumThread forumThread) {
        forumThread.setUser(userService.getUser());
        hibernateTemplate.execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Category.class);
                criteria.add(Restrictions.eq("id", parentId));
                List<Category> categories = (List<Category>) criteria.list();
                if (categories == null || categories.isEmpty()) {
                    throw new IllegalArgumentException("Category not found: " + parentId);
                }
                String content = forumThread.getContent();
                forumThread.setContent("");
                Post post = new Post();
                post.setTitle(forumThread.getTitle());
                post.setContent(content);
                post.setUser(forumThread.getUser());
                forumThread.addPost(post);
                Category category = categories.get(0);
                category.addForumThread(forumThread);
                session.saveOrUpdate(category);
                return null;
            }
        });
    }

    @Override
    @Secured(SecurityRoles.ROLE_USER)
    @Transactional
    public void insertPostEntry(final Serializable parentId, final Post post) {
        post.setUser(userService.getUser());
        hibernateTemplate.execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ForumThread.class);
                criteria.add(Restrictions.eq("id", parentId));
                List<ForumThread> forumThreads = (List<ForumThread>) criteria.list();
                if (forumThreads == null || forumThreads.isEmpty()) {
                    throw new IllegalArgumentException("ForumThread not found: " + parentId);
                }
                ForumThread forumThread = forumThreads.get(0);
                forumThread.addPost(post);
                session.saveOrUpdate(forumThread);
                return null;
            }
        });
    }

    private Date getLatestPost(Category category) {
        try {
            List list = hibernateTemplate.find("SELECT MAX(p.date) FROM com.btxtech.game.services.forum.ForumThread t, com.btxtech.game.services.forum.Post p WHERE t = p.forumThread AND t.category = ?", category);
            if (list.isEmpty()) {
                return null;
            }
            return (Date) list.get(0);
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        }
    }

    private Date getLatestPost(ForumThread forumThread) {
        try {
            List list = hibernateTemplate.find("SELECT MAX(p.date) FROM com.btxtech.game.services.forum.Post p WHERE p.forumThread = ?", forumThread);
            if (list.isEmpty()) {
                return null;
            }
            return (Date) list.get(0);
        } catch (DataAccessException e) {
            log.error("", e);
            throw e;
        }
    }

    @Override
    public int getPostCount(Category category) {
        List list = hibernateTemplate.find("SELECT COUNT(*) FROM com.btxtech.game.services.forum.ForumThread t, com.btxtech.game.services.forum.Post p WHERE t = p.forumThread AND t.category = ?", category);
        if (list.isEmpty()) {
            return 0;
        }
        return ((Long) list.get(0)).intValue();
    }

    @Override
    @Secured(SecurityRoles.ROLE_FORUM_ADMINISTRATOR)
    @Transactional
    public void delete(AbstractForumEntry abstractForumEntry) {
        try {
            hibernateTemplate.delete(abstractForumEntry);
        } catch (DataAccessException e) {
            log.error("", e);
        }
    }
}
