package com.btxtech.game.services.cms;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 11:56:29
 */
public class DbPropertyBook extends DbProperty implements ContentDataProviderInfo {
    private List<DbPropertyRow> dbPropertyRows;
    private String springBeanName;
    private String contentProviderGetter;
    private String className;
    private ContentDataProviderInfo parentContentDataProviderInfo;

    public List<DbPropertyRow> getDbPropertyRows() {
        return dbPropertyRows;
    }

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }

    @Override
    public String getContentProviderGetter() {
        return contentProviderGetter;
    }

    @Override
    public ContentDataProviderInfo getParentContentDataProvider() {
        return parentContentDataProviderInfo;
    }

    public void setParentSpringBeanProvider(ContentDataProviderInfo parentContentDataProviderInfo) {
        this.parentContentDataProviderInfo = parentContentDataProviderInfo;
    }

    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public void setContentProviderGetter(String contentProviderGetter) {
        this.contentProviderGetter = contentProviderGetter;
    }

    public void setDbPropertyRows(List<DbPropertyRow> dbPropertyRows) {
        this.dbPropertyRows = dbPropertyRows;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public Collection<? extends DbContent> getChildren() {
        return dbPropertyRows;
    }
}
