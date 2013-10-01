package com.btxtech.game.services.user;

import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.CrudChild;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 29.06.2011
 * Time: 00:39:22
 */
@Entity(name = "USER_SECURITY_CMS_PAGE_ACCESS")
public class DbPageAccessControl implements CrudChild<User> {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    private DbPage dbPage;
    @ManyToOne(optional = false)
    private User user;

    @Override
    public Serializable getId() {
        return id;
    }

    public DbPage getDbPage() {
        return dbPage;
    }

    public void setDbPage(DbPage dbPage) {
        this.dbPage = dbPage;
    }

    @Override
    public void setParent(User user) {
        this.user = user;
    }

    @Override
    public User getParent() {
        return user;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbPageAccessControl)) return false;

        DbPageAccessControl that = (DbPageAccessControl) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
