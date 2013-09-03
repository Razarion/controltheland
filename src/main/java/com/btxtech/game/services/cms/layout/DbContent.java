package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.user.UserService;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.util.Collection;

/**
 * User: beat
 * Date: 06.06.2011
 * Time: 15:26:35
 */
@Entity(name = "CMS_CONTENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class DbContent implements CrudChild<DbContent> {
    public enum Access {
        DENIED,
        ALLOWED,
        REGISTERED_USER,
        USER,
        INHERIT
    }

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbContent parent;
    private String cssClass;
    private Access readRestricted = Access.INHERIT;
    private Access writeRestricted = Access.INHERIT;
    private Access createRestricted = Access.INHERIT;
    private Access deleteRestricted = Access.INHERIT;
    private String aboveBorderCss;
    private String borderCss;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setParent(DbContent parent) {
        this.parent = parent;
    }

    @Override
    public DbContent getParent() {
        return parent;
    }

    public Collection<DbContent> getChildren() {
        return null;
    }

    public DataProviderInfo getParentContentDataProvider() {
        parent = HibernateUtil.deproxy(parent, DbContent.class);
        if (parent instanceof DataProviderInfo) {
            return (DataProviderInfo) parent;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbContent)) return false;

        DbContent dbContent = (DbContent) o;

        return id != null && id.equals(dbContent.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public String getSpringBeanName() {
        return null;
    }

    public String getNextPossibleSpringBeanName() {
        if (getSpringBeanName() != null) {
            return getSpringBeanName();
        }
        if (getParent() != null) {
            return getParent().getNextPossibleSpringBeanName();
        }
        throw new IllegalStateException("No SpringBeanName in hierarchy");
    }

    public void setSpringBeanName(String springBeanName) {
        throw new UnsupportedOperationException();
    }

    public String getContentProviderGetter() {
        return null;
    }

    public void setContentProviderGetter(String contentProviderGetter) {
        throw new UnsupportedOperationException();
    }

    public String getExpression() {
        return null;
    }

    public void setExpression(String expression) {
        throw new UnsupportedOperationException();
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public Access getReadRestricted() {
        return readRestricted;
    }

    public void setReadRestricted(Access readRestricted) {
        this.readRestricted = readRestricted;
    }

    public Access getWriteRestricted() {
        return writeRestricted;
    }

    public void setWriteRestricted(Access writeRestricted) {
        this.writeRestricted = writeRestricted;
    }

    public Access getCreateRestricted() {
        return createRestricted;
    }

    public void setCreateRestricted(Access createRestricted) {
        this.createRestricted = createRestricted;
    }

    public Access getDeleteRestricted() {
        return deleteRestricted;
    }

    public void setDeleteRestricted(Access deleteRestricted) {
        this.deleteRestricted = deleteRestricted;
    }

    public boolean hasBorderCss() {
        return borderCss != null;
    }

    public String getBorderCss() {
        return borderCss;
    }

    public void setBorderCss(String borderCss) {
        this.borderCss = borderCss;
    }

    public boolean hasAboveBorderCss() {
        return aboveBorderCss != null;
    }

    public String getAboveBorderCss() {
        return aboveBorderCss;
    }

    public void setAboveBorderCss(String aboveBorderCss) {
        this.aboveBorderCss = aboveBorderCss;
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public String toString() {
        return getClass().getName() + " id:" + id;
    }
}
