package com.btxtech.game.services.cms;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.HibernateUtil;

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
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbContent parent;

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
    public void init() {
        // TODO remove
    }

    @Override
    public void setParent(DbContent parent) {
        this.parent = parent;
    }

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

    @Override
    public String toString() {
        return getClass().getName() + " id:" + id;
    }
}
