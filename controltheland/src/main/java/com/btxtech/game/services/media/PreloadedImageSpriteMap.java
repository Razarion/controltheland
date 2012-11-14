package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
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
@Entity(name = "IMAGE_SPRITE_MAP_PRELOADED")
public class PreloadedImageSpriteMap implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private PreloadedImageSpriteMapInfo.Type type;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbImageSpriteMap dbImageSpriteMap;

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

    public PreloadedImageSpriteMapInfo.Type getType() {
        return type;
    }

    public void setType(PreloadedImageSpriteMapInfo.Type type) {
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

    public DbImageSpriteMap getDbImageSpriteMap() {
        return dbImageSpriteMap;
    }

    public void setDbImageSpriteMap(DbImageSpriteMap dbImageSpriteMap) {
        this.dbImageSpriteMap = dbImageSpriteMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreloadedImageSpriteMap that = (PreloadedImageSpriteMap) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
