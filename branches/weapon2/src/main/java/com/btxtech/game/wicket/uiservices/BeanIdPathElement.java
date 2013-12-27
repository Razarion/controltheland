package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.layout.DataProviderInfo;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.page.DbPage;

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
    private String expression;
    private boolean childDetailPage = false;
    private boolean createEditPage = false;
    private boolean invokePage = false;

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
        expression = dataProviderInfo.getExpression();
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

    public String getExpression() {
        return expression;
    }

    public boolean hasExpression() {
        return expression != null;
    }

    public boolean isChildDetailPage() {
        return childDetailPage;
    }

    public void setChildDetailPage(boolean childDetailPage) {
        this.childDetailPage = childDetailPage;
    }

    public boolean isCreateEditPage() {
        BeanIdPathElement element = this;
        do {
            if (element.createEditPage) {
                return true;
            }
            element = element.parent;
        } while (element != null);
        return false;
    }

    public void setCreateEditPage(boolean createEditPage) {
        this.createEditPage = createEditPage;
    }

    public boolean isInvokePage() {
        BeanIdPathElement element = this;
        do {
            if (element.invokePage) {
                return true;
            }
            element = element.parent;
        } while (element != null);
        return false;
    }

    public void setInvokePage(boolean invokePage) {
        this.invokePage = invokePage;
    }

    public BeanIdPathElement createChildFromDataProviderInfo(DataProviderInfo dataProviderInfo) {
        BeanIdPathElement beanIdPathElement = new BeanIdPathElement();
        beanIdPathElement.parent = this;
        beanIdPathElement.setDataProviderInfo(dataProviderInfo);
        return beanIdPathElement;
    }

    public BeanIdPathElement createChildFromBeanId(Serializable parentId) {
        BeanIdPathElement beanIdPathElement = new BeanIdPathElement();
        beanIdPathElement.parent = this;
        beanIdPathElement.setBeanId(parentId);
        return beanIdPathElement;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("BeanIdPathElement ");
        privateToString(s);
        return s.toString();
    }

    public BeanIdPathElement getFirstDbBeanElement() {
        BeanIdPathElement beanIdPathElement = this;
        BeanIdPathElement last = null;
        while (beanIdPathElement != null) {
            if (beanIdPathElement.hasSpringBeanName() && beanIdPathElement.hasContentProviderGetter()) {
                return last;
            }
            last = beanIdPathElement;
            beanIdPathElement = beanIdPathElement.getParent();
        }
        throw new IllegalStateException("No root spring bean name found");
    }

    private void privateToString(StringBuilder s) {
        s.append("[");
        if (pageId != null) {
            s.append(" pageId: ");
            s.append(pageId);
        }
        if (beanId != null) {
            s.append(" beanId: ");
            s.append(beanId.toString());
        }
        if (springBeanName != null) {
            s.append(" springBeanName: ");
            s.append(springBeanName);
        }
        if (contentProviderGetter != null) {
            s.append(" contentProviderGetter: ");
            s.append(contentProviderGetter);
        }
        if (expression != null) {
            s.append(" expression: ");
            s.append(expression);
        }
        s.append("]");
        if (parent != null) {
            parent.privateToString(s);
        }
    }
}
