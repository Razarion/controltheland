package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
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
@DiscriminatorValue("CONTENT_LIST")
public class DbContentList extends DbContent implements DataProviderInfo, CrudParent {
    // Since the parentId field on a child can not distinguishes if it belongs to dbContentBooks or dbPropertyColumns
    // mapping tables are used.
    // The DbContent table is not cleaned after removing an item (due to ManyToMany mapping). OneToMany mapping leads
    // to an exception if orderIndex is changed
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @OrderColumn(name = "orderIndex")
    @JoinTable(name = "CMS_CONTENT_CONTENT_LIST_COLUMNS",
            joinColumns = @JoinColumn(name = "contentListId"),
            inverseJoinColumns = @JoinColumn(name = "contentId"))
    private List<DbContent> dbPropertyColumns;
    private String springBeanName;
    private String contentProviderGetter;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinTable(name = "CMS_CONTENT_CONTENT_LIST_CONTENT_BOOK",
            joinColumns = @JoinColumn(name = "contentListId"),
            inverseJoinColumns = @JoinColumn(name = "contentBookId"))
    private Collection<DbContentBook> dbContentBooks;
    private Integer rowsPerPage;
    private boolean showHead;
    private String cssClassHead;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbContentCreateEdit dbContentCreateEdit;
    private Integer columnCountSingleCell;
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

    public DbContentBook getDbPropertyBook(Class theClass) {
        for (DbContentBook dbContentBook : dbContentBooks) {
            try {
                Class contentBookClass = Class.forName(dbContentBook.getClassName());
                if (contentBookClass.isAssignableFrom(theClass)) {
                    return dbContentBook;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("No DbContentBook for: " + theClass.getName());
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

    public DbContentCreateEdit getDbContentCreateEdit() {
        return dbContentCreateEdit;
    }

    public void setDbContentCreateEdit(DbContentCreateEdit dbContentCreateEdit) {
        this.dbContentCreateEdit = dbContentCreateEdit;
    }

    public boolean isShowHead() {
        return showHead;
    }

    public void setShowHead(boolean showHead) {
        this.showHead = showHead;
    }

    public String getCssClassHead() {
        return cssClassHead;
    }

    public void setCssClassHead(String cssClassHead) {
        this.cssClassHead = cssClassHead;
    }

    public Integer getColumnCountSingleCell() {
        return columnCountSingleCell;
    }

    public void setColumnCountSingleCell(Integer columnCountSingleCell) {
        this.columnCountSingleCell = columnCountSingleCell;
    }

    @Override
    public Collection<DbContent> getChildren() {
        List<DbContent> children = new ArrayList<DbContent>(dbPropertyColumns);
        children.addAll(dbContentBooks);
        if (dbContentCreateEdit != null) {
            children.add(dbContentCreateEdit);
        }
        return children;
    }

    @Override
    public void init(UserService userService) {
        dbPropertyColumns = new ArrayList<DbContent>();
        dbContentBooks = new ArrayList<DbContentBook>();
    }
}
