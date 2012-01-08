package com.btxtech.game.services.cms.impl;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.content.DbBlogEntry;
import com.btxtech.game.services.cms.content.DbHtmlContent;
import com.btxtech.game.services.cms.content.DbWikiSection;
import com.btxtech.game.services.cms.layout.DbContentDynamicHtml;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
}
