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
import com.btxtech.game.services.cms.EditMode;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentDetailLink;
import com.btxtech.game.wicket.pages.cms.ItemTypeImage;
import com.btxtech.game.wicket.pages.cms.WritePanel;
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
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private com.btxtech.game.services.connection.Session session;
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(CmsUiServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

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
                return component4ExpressionProperty(componentId, ((DbExpressionProperty) dbContent), bean, beanIdPathElement);
            } else if (dbContent instanceof DbContentDetailLink) {
                return new ContentDetailLink(componentId, (DbContentDetailLink) dbContent, beanIdPathElement);
            } else if (dbContent instanceof DbContentContainer) {
                return new ContentContainer(componentId, (DbContentContainer) dbContent, beanIdPathElement);
            } else if (dbContent instanceof DbContentBook) {
                return new ContentBook(componentId, (DbContentBook) dbContent, beanIdPathElement);
            } else if (dbContent instanceof DbStaticProperty) {
                DbStaticProperty dbStaticProperty = (DbStaticProperty) dbContent;
                Component label = new Label(componentId, dbStaticProperty.getHtml()).setEscapeModelStrings(dbStaticProperty.getEscapeMarkup());
                if (dbContent.getCssClass() != null) {
                    label.add(new SimpleAttributeModifier("class", dbContent.getCssClass()));
                }
                return label;
            } else {
                log.warn("CmsUiServiceImpl: No Wicket Component for content: " + dbContent);
                return new Label(componentId, "No content");
            }
        } catch (Exception e) {
            log.error("DbContent: " + dbContent + " bean: " + bean + " id: " + componentId + " " + beanIdPathElement, e);
            return new Label(componentId, "Error!");
        }
    }

    private Component component4ExpressionProperty(String id, DbExpressionProperty dbExpressionProperty, Object bean, BeanIdPathElement beanIdPathElement) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object value = PropertyUtils.getProperty(bean, dbExpressionProperty.getExpression());
        Component component;
        if (value instanceof DbItemType) {
            component = new ItemTypeImage(id, (DbItemType) value);
        } else {
            if (PropertyUtils.isWriteable(bean, beanIdPathElement.getExpression()) && getEditMode(dbExpressionProperty) != null) {
                // Write
                component = new WritePanel(id, value, beanIdPathElement, dbExpressionProperty);
            } else {
                // Read only
                if (value != null) {
                    String stringValue;
                    if (dbExpressionProperty.getOptionalType() != null) {
                        stringValue = typeToString(value, dbExpressionProperty.getOptionalType());
                    } else {
                        stringValue = value.toString();
                    }
                    component = new Label(id, stringValue).setEscapeModelStrings(dbExpressionProperty.getEscapeMarkup());
                } else {
                    component = new Label(id, "");
                }
            }
        }
        if (dbExpressionProperty.getCssClass() != null) {
            component.add(new SimpleAttributeModifier("class", dbExpressionProperty.getCssClass()));
        }
        return component;
    }

    private String typeToString(Object value, DbExpressionProperty.Type type) {
        switch (type) {
            case DATE_DDMMYYYY_HH_MM_SS:
                if (value instanceof Date) {
                    return WebCommon.formatDateTime((Date) value);
                } else if (value instanceof Number) {
                    return WebCommon.formatDateTime(new Date(((Number) value).longValue()));
                } else {
                    throw new IllegalArgumentException("Date value must be Number ore Date: " + value);
                }
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    @Override
    public <T extends DbContent> T getDbContent(int contentId) {
        return (T) cmsService.getDbContent(contentId);
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
            } else if (beanIdPathElement.hasExpression() && beanIdPathElement.hasParent()) {
                Object bean = getDataProviderBean(beanIdPathElement.getParent());
                return PropertyUtils.getProperty(bean, beanIdPathElement.getExpression());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setDataProviderBean(Object value, BeanIdPathElement beanIdPathElement) {
        try {
            Object object = getDataProviderBean(beanIdPathElement.getParent());
            PropertyUtils.setProperty(object, beanIdPathElement.getExpression(), value);
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

    @Override
    public EditMode getEditMode(int contentId) {
        return getEditMode(cmsService.getDbContent(contentId));
    }

    private EditMode getEditMode(DbContent dbContent) {
        EditMode editMode = session.getEditMode();
        if (editMode == null) {
            return null;
        }
        String springBeanName = dbContent.getNextPossibleSpringBeanName();

        if (springBeanName.equals(editMode.getSpringBeanName())) {
            return editMode;
        } else {
            leaveEditMode();
            return null;
        }
    }

    @Override
    public void enterEditMode(int contentId) {
        DbContent dbContent = cmsService.getDbContent(contentId);
        String springBeanName = dbContent.getSpringBeanName();
        if (springBeanName == null) {
            throw new IllegalArgumentException("No spring bean name in DbContent: " + dbContent);
        }
        EditMode editMode = new EditMode(springBeanName);
        session.setEditMode(editMode);
    }

    @Override
    public void leaveEditMode() {
        hibernateTemplate.clear();
        session.setEditMode(null);
    }

    @Override
    public boolean isEnterEditModeAllowed(int contentId) {
        if (getEditMode(contentId) != null) {
            // Already in edit mode
            return false;
        }
        DbContent dbContent = cmsService.getDbContent(contentId);
        return dbContent.getSpringBeanName() != null;
    }

    @Override
    public boolean isSaveAllowed(int contentId) {
        if (getEditMode(contentId) == null) {
            return false;
        }
        DbContent dbContent = cmsService.getDbContent(contentId);
        return dbContent.getSpringBeanName() != null;
    }

    @Override
    public void createBean(BeanIdPathElement beanIdPathElement) {
        ContentProvider contentProvider = getContentProvider(beanIdPathElement);
        contentProvider.createDbChild();
    }

    @Override
    public void deleteBean(BeanIdPathElement beanIdPathElement) {
        ContentProvider contentProvider = getContentProvider(beanIdPathElement.getParent());
        Object bean = getDataProviderBean(beanIdPathElement);
        contentProvider.deleteDbChild(bean);
    }

    @Override
    public void save(BeanIdPathElement beanIdPathElement) {
        hibernateTemplate.flush();
    }
}
