package com.btxtech.game.services.cms;

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 14:03:56
 */
@Entity
@DiscriminatorValue("BASE")
public class DbBeanTable extends DbContent implements DataProviderInfo, CrudParent {
    // Since the parentId field on a child can not distinguishes if it belongs to dbContentBooks or dbPropertyColumns
    // mapping tables are used.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinTable(name = "CMS_CONTENT_BEAN_TABLE_COLUMNS",
            joinColumns = @JoinColumn(name = "beanTableId"),
            inverseJoinColumns = @JoinColumn(name = "contentId"))
    private List<DbContent> dbPropertyColumns;
    private String springBeanName;
    private String contentProviderGetter;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinTable(name = "CMS_CONTENT_BEAN_TABLE_CONTENT_BOOK",
            joinColumns = @JoinColumn(name = "beanTableId"),
            inverseJoinColumns = @JoinColumn(name = "contentBookId"))
    private Collection<DbContentBook> dbContentBooks;
    private Integer rowsPerPage;
    @Transient
    private CrudListChildServiceHelper<DbContent> columnsCrud;
    @Transient
    private CrudChildServiceHelper<DbContentBook> contentBookCrud;

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }

    @Override
    public String getContentProviderGetter() {
        return contentProviderGetter;
    }

    @Override
    public void setContentProviderGetter(String contentProviderGetter) {
        this.contentProviderGetter = contentProviderGetter;
    }

    @Override
    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public DbContentBook getDbPropertyBook(String className) {
        for (DbContentBook dbContentBook : dbContentBooks) {
            if (dbContentBook.getClassName().equals(className)) {
                return dbContentBook;
            }
        }
        throw new IllegalArgumentException("No DbContentBook for: " + className);
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

    public CrudListChildServiceHelper<DbContent> getColumnsCrud() {
        if (columnsCrud == null) {
            columnsCrud = new CrudListChildServiceHelper<DbContent>(dbPropertyColumns, DbContent.class, this);
        }
        return columnsCrud;
    }

    public CrudChildServiceHelper<DbContentBook> getContentBookCrud() {
        if (contentBookCrud == null) {
            contentBookCrud = new CrudChildServiceHelper<DbContentBook>(dbContentBooks, DbContentBook.class, this);
        }
        return contentBookCrud;
    }

    @Override
    public Collection<DbContent> getChildren() {
        List<DbContent> children = new ArrayList<DbContent>(dbPropertyColumns);
        children.addAll(dbContentBooks);
        return children;
    }

    @Override
    public void init() {
        dbPropertyColumns = new ArrayList<DbContent>();
        dbContentBooks = new ArrayList<DbContentBook>();
    }
}
