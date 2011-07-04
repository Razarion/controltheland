package com.btxtech.game.services.cms.impl;

import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.DbBlogEntry;
import com.btxtech.game.services.cms.DbWikiSection;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void init() {
        blogEntryCrudRootServiceHelper.init(DbBlogEntry.class, "timeStamp", false, false);
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
}
