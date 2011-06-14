package com.btxtech.game.services.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 14:03:56
 */
public class DbBeanTable extends DbProperty implements ContentDataProviderInfo {
    private List<DbProperty> dbPropertyColumns;
    private String springBeanName;
    private String contentProviderGetter;
    private Collection<DbPropertyBook> dbPropertyBooks;
    private ContentDataProviderInfo parentContentDataProviderInfo;
    private Integer rowsPerPage;

    public List<DbProperty> getDbPropertyColumns() {
        return dbPropertyColumns;
    }

    public int getColumnCount() {
        return dbPropertyColumns.size();
    }

    public void setDbPropertyColumns(List<DbProperty> dbPropertyColumns) {
        this.dbPropertyColumns = dbPropertyColumns;
    }

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }

    @Override
    public String getContentProviderGetter() {
        return contentProviderGetter;
    }

    public void setParentSpringBeanProvider(ContentDataProviderInfo parentContentDataProviderInfo) {
        this.parentContentDataProviderInfo = parentContentDataProviderInfo;
    }

    @Override
    public ContentDataProviderInfo getParentContentDataProvider() {
        return parentContentDataProviderInfo;
    }

    public ContentDataProviderInfo getParentContentProvider() {
        return parentContentDataProviderInfo;
    }

    public void setContentProviderGetter(String contentProviderGetter) {
        this.contentProviderGetter = contentProviderGetter;
    }

    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public void setDbPropertyBooks(Collection<DbPropertyBook> dbPropertyBooks) {
        this.dbPropertyBooks = dbPropertyBooks;
    }

    public DbPropertyBook getDbPropertyBook(String className) {
        for (DbPropertyBook dbPropertyBook : dbPropertyBooks) {
            if (dbPropertyBook.getClassName().equals(className)) {
                return dbPropertyBook;
            }
        }
        throw new IllegalArgumentException("No DbPropertyBook for: " + className);
    }

    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

    public boolean isPageable() {
        return rowsPerPage != null;
    }

    @Override
    public Collection<? extends DbContent> getChildren() {
        List<DbContent> children = new ArrayList<DbContent>(dbPropertyColumns);
        if (dbPropertyBooks != null) {
            children.addAll(dbPropertyBooks);
        }
        return children;
    }
}
