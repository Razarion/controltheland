package com.btxtech.game.services.cms;

import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 22.06.2011
 * Time: 13:12:04
 */
public interface ContentService {
    CrudRootServiceHelper<DbBlogEntry> getBlogEntryCrudRootServiceHelper();

    CrudRootServiceHelper<DbWikiSection> getWikiSectionCrudRootServiceHelper();

    String getDynamicHtml(int contentId);

    void setDynamicHtml(int contentId, String value);
}
