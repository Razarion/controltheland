package com.btxtech.game.wicket.uiservices.cms.impl;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.ContentDataProviderInfo;
import com.btxtech.game.services.cms.DbBeanTable;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbExpressionProperty;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.cms.DbPropertyBook;
import com.btxtech.game.services.cms.DbPropertyBookLink;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.CmsPageLinkPanel;
import com.btxtech.game.wicket.pages.cms.ItemTypeImage;
import com.btxtech.game.wicket.pages.cms.content.BeanTable;
import com.btxtech.game.wicket.pages.cms.content.PropertyBook;
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
    public Component getRootContent(DbPage dbPage, DbContent dbContent, String id, PageParameters pageParameters) {
        if (pageParameters.containsKey(CmsPage.CHILD_ID) && dbContent instanceof DbBeanTable) {
            ///////
            // TODO slow
            int childId = pageParameters.getInt(CmsPage.CHILD_ID);
            DbBeanTable dbBeanTable = (DbBeanTable) dbContent;
            Object bean = getContentDataProviderBean(dbBeanTable, childId);
            DbPropertyBook dbPropertyBook = dbBeanTable.getDbPropertyBook(bean.getClass().getName());
            ///////
            return new PropertyBook(id, dbPropertyBook, childId);
        }
        return getContent(dbContent, dbPage, id, null);
    }

    @Override
    public Component getContent(DbContent dbContent, Object bean, String id, Integer childId) {
        try {
            if (dbContent instanceof DbBeanTable) {
                DbBeanTable dbBeanTable = (DbBeanTable) dbContent;
                return new BeanTable(id, dbBeanTable, childId);
            } else if (dbContent instanceof DbExpressionProperty) {
                Object value = PropertyUtils.getProperty(bean, ((DbExpressionProperty) dbContent).getExpression());
                return componentForClass(id, value, ((DbExpressionProperty) dbContent).getEscapeMarkup());
            } else if (dbContent instanceof DbPropertyBookLink) {
                return new CmsPageLinkPanel(id, (DbPropertyBookLink) dbContent, bean);
            } else {
                log.warn("No Wicket Component for content: " + dbContent);
                return new Label(id, "No content");
            }
        } catch (Exception e) {
            log.error("", e);
            return new Label(id, "Error!");
        }
    }

    @Override
    public Component getContent(int contentId, Object bean, String id, Integer childId) {
        return getContent(this.<DbContent>getContentStructure(contentId), bean, id, childId);
    }

    private Component componentForClass(String id, Object value, boolean escapeMarkup) {
        if (value instanceof DbItemType) {
            return new ItemTypeImage(id, (DbItemType) value);
        } else {
            return new Label(id, value.toString()).setEscapeModelStrings(escapeMarkup);
        }
    }

    @Override
    public <T extends DbContent> T getContentStructure(int contentId) {
        return (T) cmsService.getContentStructure(contentId);
    }

    private ContentProvider getContentDataProviderBeanFormBean(Object bean, ContentDataProviderInfo contentDataProviderInfo) {
        while (contentDataProviderInfo.getContentProviderGetter() == null) {
            contentDataProviderInfo = contentDataProviderInfo.getParentContentDataProvider();
            if (contentDataProviderInfo == null) {
                throw new IllegalStateException();
            }
        }
        try {
            Method method = bean.getClass().getMethod(contentDataProviderInfo.getContentProviderGetter());
            return (ContentProvider) method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getSpringBean(ContentDataProviderInfo contentDataProviderInfo) {
        while (contentDataProviderInfo.getSpringBeanName() == null) {
            contentDataProviderInfo = contentDataProviderInfo.getParentContentDataProvider();
            if (contentDataProviderInfo == null) {
                throw new IllegalStateException();
            }
        }
        try {
            return applicationContext.getBean(contentDataProviderInfo.getSpringBeanName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getContentDataProviderBean(ContentDataProviderInfo contentDataProviderInfo, Integer childId) {
        Object contentProviderBean = getSpringBean(contentDataProviderInfo);
        if(childId != null) {
            ContentProvider contentProvider = getContentDataProviderBeanFormBean(contentProviderBean, contentDataProviderInfo);
            contentProviderBean = contentProvider.readDbChild(childId);
        }
        return contentProviderBean;
    }

    @Override
    public List getChildContentDataProviderBeans(ContentDataProviderInfo contentDataProviderInfo, Integer childId) {
        Object contentProviderBean = getSpringBean(contentDataProviderInfo);
        if (childId != null) {
            ContentProvider parentContentProvider = getContentDataProviderBeanFormBean(contentProviderBean, contentDataProviderInfo.getParentContentDataProvider());
            contentProviderBean = parentContentProvider.readDbChild(childId);
        }
        ContentProvider contentProvider = getContentDataProviderBeanFormBean(contentProviderBean, contentDataProviderInfo);
        return new ArrayList(contentProvider.readDbChildren());
    }
}
