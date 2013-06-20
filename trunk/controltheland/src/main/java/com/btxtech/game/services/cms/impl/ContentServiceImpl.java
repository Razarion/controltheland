package com.btxtech.game.services.cms.impl;

import com.btxtech.game.jsre.client.dialogs.news.NewsEntryInfo;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.content.DbBlogEntry;
import com.btxtech.game.services.cms.content.DbHtmlContent;
import com.btxtech.game.services.cms.content.DbWikiSection;
import com.btxtech.game.services.cms.layout.DbContentDynamicHtml;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * User: beat
 * Date: 22.06.2011
 * Time: 13:12:37
 */
@Component("contentService")
public class ContentServiceImpl implements ContentService {
    @Autowired
    private CrudRootServiceHelper<DbBlogEntry> blogEntryCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbWikiSection> wikiSectionCrudRootServiceHelper;
    @Autowired
    private CmsService cmsService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private UserService userService;
    private Log log = LogFactory.getLog(ContentServiceImpl.class);

    @PostConstruct
    public void init() {
        blogEntryCrudRootServiceHelper.init(DbBlogEntry.class, "timeStamp", false, false, null);
        wikiSectionCrudRootServiceHelper.init(DbWikiSection.class);
    }

    @Override
    public CrudRootServiceHelper<DbBlogEntry> getBlogEntryCrudRootServiceHelper() {
        return blogEntryCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbWikiSection> getWikiSectionCrudRootServiceHelper() {
        return wikiSectionCrudRootServiceHelper;
    }

    private DbHtmlContent getDbHtmlContent(final int contentId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(DbHtmlContent.class);
        criteria.add(Restrictions.eq("dbContentDynamicHtml", cmsService.getDbContent(contentId)));
        List list = criteria.list();
        if (list.isEmpty()) {
            DbHtmlContent dbHtmlContent = new DbHtmlContent();
            dbHtmlContent.setDbContentDynamicHtml((DbContentDynamicHtml) cmsService.getDbContent(contentId));
            dbHtmlContent.setHtml("No Content");
            session.save(dbHtmlContent);
            return dbHtmlContent;
        } else {
            if (list.size() > 1) {
                log.warn("More than one DbHtmlContent found for dbContentDynamicHtml: " + contentId);
            }
            return (DbHtmlContent) list.get(0);
        }
    }

    @Override
    @Transactional
    public String getDynamicHtml(final int contentId) {
        return getDbHtmlContent(contentId).getHtml();
    }

    @Override
    @Transactional
    public void setDynamicHtml(int contentId, String value) {
        DbHtmlContent dbHtmlContent = getDbHtmlContent(contentId);
        dbHtmlContent.setHtml(value);
        sessionFactory.getCurrentSession().update(dbHtmlContent);
    }

    @Override
    public NewsEntryInfo getNewsEntry(int index) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(DbBlogEntry.class);
        criteria.addOrder(Order.desc("timeStamp"));
        criteria.setMaxResults(1);
        criteria.setFirstResult(index);
        List list = criteria.list();
        if (list.isEmpty()) {
            return null;
        } else {
            User user = userService.getUser();
            if (user != null && index == 0) {
                UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
                userAttentionPacket.setNews(UserAttentionPacket.Type.CLEAR);
                planetSystemService.sendPacket(userService.getUserState(), userAttentionPacket);
                userService.updateLastNews(user);
            }
            return ((DbBlogEntry) list.get(0)).createNewsEntryInfo(getNewsEntryCount());
        }
    }

    public int getNewsEntryCount() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(DbBlogEntry.class);
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue();
    }

    @Override
    public void fillUserAttentionPacket(User user, UserAttentionPacket userAttentionPacket) {
        if (user == null) {
            return;
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBlogEntry.class);
        if (user.getLastNews() != null) {
            criteria.add(Restrictions.gt("timeStamp", user.getLastNews().getTime()));
        }
        criteria.setProjection(Projections.rowCount());
        if (((Number) criteria.list().get(0)).intValue() >= 1) {
            userAttentionPacket.setNews(UserAttentionPacket.Type.RAISE);
        }
    }

    @Override
    @Transactional
    public void createNewsEntryAndSendUserAttentionPacket(String title, String content) {
        // Create entry
        DbBlogEntry dbBlogEntry = blogEntryCrudRootServiceHelper.createDbChild();
        dbBlogEntry.setName(title);
        dbBlogEntry.setHtml(content);
        blogEntryCrudRootServiceHelper.updateDbChild(dbBlogEntry);
        // Create user attention
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        userAttentionPacket.setNews(UserAttentionPacket.Type.RAISE);
        planetSystemService.sendPacket(userAttentionPacket);
    }
}
