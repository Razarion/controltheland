package com.btxtech.game.services.user;

import com.btxtech.game.services.cms.layout.DbContent;
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
 * Time: 00:37:55
 */
@Entity(name = "USER_SECURITY_CMS_CONTENT_ACCESS")
public class DbContentAccessControl implements CrudChild<User> {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    private DbContent dbContent;
    private boolean readAllowed;
    private boolean writeAllowed;
    private boolean createAllowed;
    private boolean deleteAllowed;
    @ManyToOne(optional = false)
    private User user;

    @Override
    public Serializable getId() {
        return id;
    }

    public DbContent getDbContent() {
        return dbContent;
    }

    public void setDbContent(DbContent dbContent) {
        this.dbContent = dbContent;
    }

    public boolean isReadAllowed() {
        return readAllowed;
    }

    public void setReadAllowed(boolean readAllowed) {
        this.readAllowed = readAllowed;
    }

    public boolean isWriteAllowed() {
        return writeAllowed;
    }

    public void setWriteAllowed(boolean writeAllowed) {
        this.writeAllowed = writeAllowed;
    }

    public boolean isCreateAllowed() {
        return createAllowed;
    }

    public void setCreateAllowed(boolean createAllowed) {
        this.createAllowed = createAllowed;
    }

    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
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
    public void setParent(User user) {
        this.user = user;
    }

    @Override
    public User getParent() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbContentAccessControl)) return false;

        DbContentAccessControl that = (DbContentAccessControl) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
