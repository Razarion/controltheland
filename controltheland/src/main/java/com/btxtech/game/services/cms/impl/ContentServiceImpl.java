package com.btxtech.game.services.cms.impl;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.DbBlogEntry;
import com.btxtech.game.services.cms.DbContentDynamicHtml;
import com.btxtech.game.services.cms.DbHtmlContent;
import com.btxtech.game.services.cms.DbWikiSection;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
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
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(ContentServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

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
        DbHtmlContent dbHtmlContent = hibernateTemplate.execute(new HibernateCallback<DbHtmlContent>() {
            @Override
            public DbHtmlContent doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbHtmlContent.class);
                criteria.add(Restrictions.eq("dbContentDynamicHtml", cmsService.getDbContent(contentId)));
                List list = criteria.list();
                if (list.isEmpty()) {
                    return null;
                } else {
                    if (list.size() > 1) {
                        log.warn("More than one DbHtmlContent found for dbContentDynamicHtml: " + contentId);
                    }
                    return (DbHtmlContent) list.get(0);
                }
            }
        });
        if (dbHtmlContent == null) {
            dbHtmlContent = new DbHtmlContent();
            dbHtmlContent.setDbContentDynamicHtml((DbContentDynamicHtml) cmsService.getDbContent(contentId));
            dbHtmlContent.setHtml("No Content");
            hibernateTemplate.save(dbHtmlContent);
        }
        return dbHtmlContent;
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
        hibernateTemplate.update(dbHtmlContent);
    }
}
