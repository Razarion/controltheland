package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.DataProviderInfo;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.common.CrudChild;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.06.2011
 * Time: 16:44:27
 */
public class BeanIdPathElement implements Serializable {
    private Integer pageId;
    private BeanIdPathElement parent;
    private Serializable beanId;
    private String springBeanName;
    private String contentProviderGetter;

    public BeanIdPathElement(DbPage dbPage, DbContent dbContent) {
        pageId = dbPage.getId();
        if (dbContent instanceof DataProviderInfo) {
            setDataProviderInfo((DataProviderInfo) dbContent);
        }
    }

    private BeanIdPathElement() {
    }

    public void setDataProviderInfo(DataProviderInfo dataProviderInfo) {
        springBeanName = dataProviderInfo.getSpringBeanName();
        contentProviderGetter = dataProviderInfo.getContentProviderGetter();
    }

    public void setBeanId(Serializable beanId) {
        this.beanId = beanId;
    }

    public BeanIdPathElement getParent() {
        return parent;
    }

    public Serializable getBeanId() {
        return beanId;
    }

    public String getSpringBeanName() {
        return springBeanName;
    }

    public String getContentProviderGetter() {
        return contentProviderGetter;
    }

    public boolean hasBeanId() {
        return beanId != null;
    }

    public boolean hasSpringBeanName() {
        return springBeanName != null;
    }

    public boolean hasContentProviderGetter() {
        return contentProviderGetter != null;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public int getPageId() {
        BeanIdPathElement element = this;
        while (element.parent != null) {
            element = element.parent;
        }
        return element.pageId;
    }

    public BeanIdPathElement createChild(DataProviderInfo dataProviderInfo, Object parentBean) {
        BeanIdPathElement beanIdPathElement = new BeanIdPathElement();
        beanIdPathElement.parent = this;
        if (dataProviderInfo != null) {
            beanIdPathElement.setDataProviderInfo(dataProviderInfo);
        }
        if (parentBean instanceof CrudChild) {
            beanIdPathElement.setBeanId(((CrudChild) parentBean).getId());
        }
        return beanIdPathElement;
    }

    public BeanIdPathElement createChild(Serializable parentId) {
        BeanIdPathElement beanIdPathElement = new BeanIdPathElement();
        beanIdPathElement.parent = this;
        beanIdPathElement.setBeanId(parentId);
        return beanIdPathElement;
    }

    @Override
    public String toString() {
        return "BeanIdPathElement " + privateToString();
    }

    private String privateToString() {
        return "[pageId: " + pageId + " beanId: " + beanId + " springBeanName:" + springBeanName + " contentProviderGetter:" + contentProviderGetter + "]" + (parent != null ? parent.privateToString() : "");
    }
}
