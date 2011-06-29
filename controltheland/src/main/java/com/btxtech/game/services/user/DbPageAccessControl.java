package com.btxtech.game.services.user;

import com.btxtech.game.services.cms.DbPage;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 29.06.2011
 * Time: 00:39:22
 */
@Entity(name = "USER_SECURITY_CMS_PAGE_ACCESS")
public class DbPageAccessControl {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    private DbPage dbPage;
    @ManyToOne(optional = false)
    private User user;

    public DbPage getDbPage() {
        return dbPage;
    }

    public void setDbPage(DbPage dbPage) {
        this.dbPage = dbPage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
