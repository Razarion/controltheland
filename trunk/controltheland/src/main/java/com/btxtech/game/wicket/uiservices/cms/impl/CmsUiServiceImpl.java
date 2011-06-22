package com.btxtech.game.wicket.uiservices.cms.impl;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentBook;
import com.btxtech.game.services.cms.DbContentContainer;
import com.btxtech.game.services.cms.DbContentDetailLink;
import com.btxtech.game.services.cms.DbContentList;
import com.btxtech.game.services.cms.DbExpressionProperty;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.cms.DbStaticProperty;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentDetailLink;
import com.btxtech.game.wicket.pages.cms.ItemTypeImage;
import com.btxtech.game.wicket.pages.cms.content.ContentBook;
import com.btxtech.game.wicket.pages.cms.content.ContentContainer;
import com.btxtech.game.wicket.pages.cms.content.ContentList;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 16:55:27
 */
@org.springframework.stereotype.Component("cmsUiService")
public class CmsUiServiceImpl implements CmsUiService {
    @Autowired
    private CmsService cmsService;
    @Autowired
    private ApplicationContext applicationContext;
    private Log log = LogFactory.getLog(CmsUiServiceImpl.class);

    @Override
    public Component getRootComponent(DbPage dbPage, String componentId, PageParameters pageParameters) {
        DbContent dbContent = dbPage.getContent();
        BeanIdPathElement beanIdPathElement = new BeanIdPathElement(dbPage, dbContent);
        // if the Page should display a child of a ContentList
        if (pageParameters.containsKey(CmsPage.CHILD_ID) && dbContent instanceof DbContentList) {
            beanIdPathElement = beanIdPathElement.createChild(pageParameters.getInt(CmsPage.CHILD_ID));
            Object bean = getDataProviderBean(beanIdPathElement);
            dbContent = ((DbContentList) dbContent).getDbPropertyBook(bean.getClass().getName());
        }
        return getComponent(dbContent, null, componentId, beanIdPathElement);
    }

    @Override
    public Component getComponent(DbContent dbContent, Object bean, String componentId, BeanIdPathElement beanIdPathElement) {
        try {
            if (dbContent instanceof DbContentList) {
                return new ContentList(componentId, (DbContentList) dbContent, beanIdPathElement);
            } else if (dbContent instanceof DbExpressionProperty) {
                Object value = PropertyUtils.getProperty(bean, ((DbExpressionProperty) dbContent).getExpression());
                return componentForClass(componentId, value, ((DbExpressionProperty) dbContent).getEscapeMarkup());
            } else if (dbContent instanceof DbContentDetailLink) {
                return new ContentDetailLink(componentId, (DbContentDetailLink) dbContent, beanIdPathElement);
            } else if (dbContent instanceof DbContentContainer) {
                return new ContentContainer(componentId, (DbContentContainer) dbContent, beanIdPathElement);
            } else if (dbContent instanceof DbContentBook) {
                return new ContentBook(componentId, (DbContentBook) dbContent, beanIdPathElement);
            } else if (dbContent instanceof DbStaticProperty) {
                DbStaticProperty dbStaticProperty = (DbStaticProperty) dbContent;
                return new Label(componentId, dbStaticProperty.getHtml()).setEscapeModelStrings(dbStaticProperty.getEscapeMarkup());
            } else {
                log.warn("CmsUiServiceImpl: No Wicket Component for content: " + dbContent);
                return new Label(componentId, "No content");
            }
        } catch (Exception e) {
            log.error("DbContent: " + dbContent + " bean: " + bean + " id: " + componentId + " " + beanIdPathElement, e);
            return new Label(componentId, "Error!");
        }
    }

    private Component componentForClass(String id, Object value, boolean escapeMarkup) {
        if (value instanceof DbItemType) {
            return new ItemTypeImage(id, (DbItemType) value);
        } else {
            return new Label(id, value.toString()).setEscapeModelStrings(escapeMarkup);
        }
    }

    @Override
    public <T extends DbContent> T getDbContent(int contentId) {
        return (T) cmsService.getContentStructure(contentId);
    }

    @Override
    public Object getDataProviderBean(BeanIdPathElement beanIdPathElement) {
        try {
            if (beanIdPathElement.hasSpringBeanName()) {
                return applicationContext.getBean(beanIdPathElement.getSpringBeanName());
            } else if (beanIdPathElement.hasContentProviderGetter() && beanIdPathElement.hasBeanId() && beanIdPathElement.hasParent()) {
                Object bean = getDataProviderBean(beanIdPathElement.getParent());
                Method method = bean.getClass().getMethod(beanIdPathElement.getContentProviderGetter());
                ContentProvider contentProvider = (ContentProvider) method.invoke(bean);
                return contentProvider.readDbChild(beanIdPathElement.getBeanId());
            } else if (beanIdPathElement.hasBeanId() && beanIdPathElement.hasParent()) {
                ContentProvider contentProvider = getContentProvider(beanIdPathElement.getParent());
                return contentProvider.readDbChild(beanIdPathElement.getBeanId());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List getDataProviderBeans(BeanIdPathElement beanIdPathElement) {
        try {
            ContentProvider contentProvider;
            if (beanIdPathElement.hasContentProviderGetter() && !beanIdPathElement.hasSpringBeanName() && !beanIdPathElement.hasBeanId()) {
                Object bean = getDataProviderBean(beanIdPathElement.getParent());
                Method method = bean.getClass().getMethod(beanIdPathElement.getContentProviderGetter());
                contentProvider = (ContentProvider) method.invoke(bean);
            } else {
                contentProvider = getContentProvider(beanIdPathElement);
            }
            return new ArrayList(contentProvider.readDbChildren());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private ContentProvider getContentProvider(BeanIdPathElement beanIdPathElement) {
        try {
            if (beanIdPathElement.hasContentProviderGetter() && beanIdPathElement.hasSpringBeanName()) {
                Object bean = applicationContext.getBean(beanIdPathElement.getSpringBeanName());
                Method method = bean.getClass().getMethod(beanIdPathElement.getContentProviderGetter());
                return (ContentProvider) method.invoke(bean);
            } else if (beanIdPathElement.hasContentProviderGetter() && beanIdPathElement.hasBeanId() && beanIdPathElement.hasParent()) {
                ContentProvider contentProvider = getContentProvider(beanIdPathElement.getParent());
                Object bean = contentProvider.readDbChild(beanIdPathElement.getBeanId());
                Method method = bean.getClass().getMethod(beanIdPathElement.getContentProviderGetter());
                return (ContentProvider) method.invoke(bean);
            } else {
                throw new IllegalArgumentException(beanIdPathElement.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
