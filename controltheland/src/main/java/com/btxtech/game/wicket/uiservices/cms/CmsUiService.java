package com.btxtech.game.wicket.uiservices.cms;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentBook;
import com.btxtech.game.services.cms.DbContentInvoker;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.cms.EditMode;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 15:43:28
 */
public interface CmsUiService {
    PageParameters getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage predefinedType);

    PageParameters createPageParametersFromBeanId(BeanIdPathElement beanIdPathElement);

    CmsPage getPredefinedNotFound();

    void setupPredefinedUrls();

    Map<CmsUtil.CmsPredefinedPage, String> getPredefinedUrls();

    void setPredefinedResponsePage(Component component, CmsUtil.CmsPredefinedPage predefinedType);

    void setMessageResponsePage(Component component, String message);

    void setResponsePage(Component component, int dbPageId);

    void setInvokerResponsePage(Component component, int dbPageId, DbContentInvoker dbContentInvoker);

    void setParentResponsePage(Component component, DbContent dbContent, BeanIdPathElement beanIdPathElement);

    Component getComponent(DbContent dbContent, Object bean, String id, BeanIdPathElement parentBeanIdPathElement, ContentContext contentContext);

    Component getRootComponent(DbPage dbPage, String id, ContentContext contentContext);

    <T extends DbContent> T getDbContent(int contentId);

    Object getValue(String springBeanName, String propertyExpression);

    Object getDataProviderBean(BeanIdPathElement beanIdPathElement);

    void setDataProviderBean(Object value, BeanIdPathElement beanIdPathElement, int contentId);

    List getDataProviderBeans(BeanIdPathElement beanIdPathElement, int contentId, ContentContext contentContext);

    String getSortInfo(String columnName, int contentListId, ContentContext contentContext);

    EditMode getEditMode(int contentId);

    void enterEditMode(int contentId, BeanIdPathElement beanIdPathElement);

    void leaveEditMode();

    boolean isEnterEditModeAllowed(int contentId, BeanIdPathElement beanIdPathElement);

    boolean isSaveAllowed(int contentId, BeanIdPathElement beanIdPathElement);

    boolean isCreateEditAllowed(int contentCreateEditId);

    void save(BeanIdPathElement beanIdPathElement);

    void deleteBean(BeanIdPathElement beanIdPathElement);

    CrudChild createBean(BeanIdPathElement beanIdPathElement);

    CrudChild createAndFillBean(BeanIdPathElement beanIdPathElement);

    boolean isReadAllowed(int contentId);

    boolean isAllowedGeneric(DbContent.Access access);

    boolean isPageAccessAllowed(DbPage dbPage);

    void invokeCall(int contentId, BeanIdPathElement beanIdPathElement);

    boolean isConditionFulfilled(int contentId, BeanIdPathElement beanIdPathElement);

    SecurityCmsUiService getSecurityCmsUiService();

    BeanIdPathElement createChildBeanIdPathElement(DbContent childDbContent, BeanIdPathElement beanIdPathElement, CrudChild crudChild);

    void invoke(DbContentInvoker dbContentInvoker, HashMap<String, String> parameters) throws InvocationTargetException;

    void invokeHiddenMethod(DbContentBook dbContentBook, BeanIdPathElement beanIdPathElement);
}
