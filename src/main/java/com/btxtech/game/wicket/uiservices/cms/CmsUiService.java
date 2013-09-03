package com.btxtech.game.wicket.uiservices.cms;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.EditMode;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentInvoker;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import org.apache.wicket.Component;
import org.apache.wicket.request.mapper.parameter.PageParameters;

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

    PageParameters getPreviousPageParameters(BeanIdPathElement beanIdPathElement, int contentId, ContentContext contentContext);

    PageParameters getNextPageParameters(BeanIdPathElement beanIdPathElement, int contentId, ContentContext contentContext);

    PageParameters getUpPageParameters(BeanIdPathElement beanIdPathElement);

    CmsPage getPredefinedNotFound();

    void setupPredefinedUrls();

    Map<CmsUtil.CmsPredefinedPage, String> getPredefinedUrls();

    void setPredefinedResponsePage(Component component, CmsUtil.CmsPredefinedPage predefinedType);

    void setPredefinedResponsePage(Component component, CmsUtil.CmsPredefinedPage predefinedType, String additionalParameter);

    void setMessageResponsePage(Component component, String key, String additionalParameter);

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

    String getFacebookAppSecret();

    String getFacebookAppId();

    String getFacebookRedirectUri();

    String getUrl4CmsPage(CmsUtil.CmsPredefinedPage predefinedType);
}
