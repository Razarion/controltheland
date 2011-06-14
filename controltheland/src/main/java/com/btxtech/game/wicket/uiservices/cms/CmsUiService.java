package com.btxtech.game.wicket.uiservices.cms;

import com.btxtech.game.services.cms.ContentDataProviderInfo;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbPage;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;

import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 15:43:28
 */
public interface CmsUiService {

    Component getContent(DbContent dbContent, Object bean, String id, Integer childId);

    Component getContent(int contentId, Object bean, String id, Integer childId);

    Component getRootContent(DbPage dbPage, DbContent dbContent, String id, PageParameters pageParameters);

    <T extends DbContent> T getContentStructure(int contentId);

    Object getContentDataProviderBean(ContentDataProviderInfo contentDataProviderInfo, Integer childId);

    List getChildContentDataProviderBeans(ContentDataProviderInfo contentDataProviderInfo, Integer childId);
}
