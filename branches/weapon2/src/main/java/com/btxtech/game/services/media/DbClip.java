package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 23:02
 */
@Entity(name = "CLIP_LIBRARY")
public class DbClip implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbSound dbSound;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbImageSpriteMap dbImageSpriteMap;

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
    public void init(UserService userService) {
    }

    @Override
    public void setParent(Object o) {
        // Ignore
    }

    @Override
    public Object getParent() {
        return null;
    }

    public DbSound getDbSound() {
        return dbSound;
    }

    public void setDbSound(DbSound dbSound) {
        this.dbSound = dbSound;
    }

    public DbImageSpriteMap getDbImageSpriteMap() {
        return dbImageSpriteMap;
    }

    public void setDbImageSpriteMap(DbImageSpriteMap dbImageSpriteMap) {
        this.dbImageSpriteMap = dbImageSpriteMap;
    }

    public ClipInfo createClipInfo() {
        if (dbImageSpriteMap == null) {
            throw new IllegalStateException("dbImageSpriteMap is not set. Clip Id: " + id);
        }
        ClipInfo clipInfo = new ClipInfo(id);
        clipInfo.setSpriteMapId(dbImageSpriteMap.getId());
        if (dbSound != null) {
            clipInfo.setSoundId(dbSound.getId());
        }
        return clipInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbClip that = (DbClip) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
