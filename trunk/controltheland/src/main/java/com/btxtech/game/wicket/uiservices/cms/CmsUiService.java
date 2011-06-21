package com.btxtech.game.wicket.uiservices.cms;

import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;

import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 15:43:28
 */
public interface CmsUiService {

    Component getComponent(DbContent dbContent, Object bean, String id, BeanIdPathElement parentBeanIdPathElement);

    Component getRootComponent(DbPage dbPage, String id, PageParameters pageParameters);

    <T extends DbContent> T getDbContent(int contentId);

    Object getDataProviderBean(BeanIdPathElement beanIdPathElement);

    List getDataProviderBeans(BeanIdPathElement beanIdPathElement);
}
