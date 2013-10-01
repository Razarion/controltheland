package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 11:56:29
 */
@Entity
@DiscriminatorValue("CONTENT_BOOK")
public class DbContentBook extends DbContent implements DataProviderInfo, CrudParent {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", base = 0)
    private List<DbContentRow> dbContentRows;
    private String className;
    private String springBeanName;
    private String contentProviderGetter;
    private boolean showName = true;
    private String hiddenMethodName;
    private boolean navigationVisible;
    private String upNavigationName;
    private String previousNavigationName;
    private String nextNavigationName;
    private String navigationCssClass;
    @Transient
    private CrudListChildServiceHelper<DbContentRow> rowCrud;

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }

    @Override
    public String getContentProviderGetter() {
        return contentProviderGetter;
    }

    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public void setContentProviderGetter(String contentProviderGetter) {
        this.contentProviderGetter = contentProviderGetter;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    public String getHiddenMethodName() {
        return hiddenMethodName;
    }

    public void setHiddenMethodName(String hiddenMethodName) {
        this.hiddenMethodName = hiddenMethodName;
    }

    @Override
    public Collection<DbContent> getChildren() {
        return new ArrayList<DbContent>(dbContentRows);
    }

    public CrudListChildServiceHelper<DbContentRow> getRowCrud() {
        if (rowCrud == null) {
            rowCrud = new CrudListChildServiceHelper<>(dbContentRows, DbContentRow.class, this);
        }
        return rowCrud;
    }

    @Override
    public void init(UserService userService) {
        dbContentRows = new ArrayList<>();
    }

    public boolean isNavigationVisible() {
        return navigationVisible;
    }

    public void setNavigationVisible(boolean navigationVisible) {
        this.navigationVisible = navigationVisible;
    }

    public String getPreviousNavigationName() {
        return previousNavigationName;
    }

    public void setPreviousNavigationName(String previousNavigationName) {
        this.previousNavigationName = previousNavigationName;
    }

    public String getUpNavigationName() {
        return upNavigationName;
    }

    public void setUpNavigationName(String upNavigationName) {
        this.upNavigationName = upNavigationName;
    }

    public String getNextNavigationName() {
        return nextNavigationName;
    }

    public void setNextNavigationName(String nextNavigationName) {
        this.nextNavigationName = nextNavigationName;
    }

    public String getNavigationCssClass() {
        return navigationCssClass;
    }

    public void setNavigationCssClass(String navigationCssClass) {
        this.navigationCssClass = navigationCssClass;
    }
}
