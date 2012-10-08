package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.CommonClipInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 15.08.12
 * Time: 13:50
 */
@Entity(name = "CLIP_COMMON")
public class DbCommonClip implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private CommonClipInfo.Type type;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbClip dbClip;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void init(UserService userService) {
        // Not used
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public CommonClipInfo.Type getType() {
        return type;
    }

    public void setType(CommonClipInfo.Type type) {
        this.type = type;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public DbClip getDbClip() {
        return dbClip;
    }

    public void setDbClip(DbClip dbClip) {
        this.dbClip = dbClip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbCommonClip that = (DbCommonClip) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
