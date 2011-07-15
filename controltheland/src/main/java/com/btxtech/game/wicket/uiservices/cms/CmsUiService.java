package com.btxtech.game.wicket.uiservices.cms;

import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.cms.EditMode;
import com.btxtech.game.services.common.CrudChild;
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

    void setDataProviderBean(Object value, BeanIdPathElement beanIdPathElement, int contentId);

    List getDataProviderBeans(BeanIdPathElement beanIdPathElement);

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

    boolean isPageAccessAllowed(DbPage dbPage);

    void invokeCall(int contentId, BeanIdPathElement beanIdPathElement);

    boolean isConditionFulfilled(int contentId, BeanIdPathElement beanIdPathElement);
}
