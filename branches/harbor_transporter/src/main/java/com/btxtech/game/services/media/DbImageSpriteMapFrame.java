package com.btxtech.game.services.media;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 23:02
 */
@Entity(name = "IMAGE_SPRITE_MAP_FRAME")
public class DbImageSpriteMapFrame implements CrudChild<DbImageSpriteMap> {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 500000)
    private byte[] data;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbImageSpriteMap dbImageSpriteMap;

    @Override
    public Integer getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(DbImageSpriteMap dbImageSpriteMap) {
        this.dbImageSpriteMap = dbImageSpriteMap;
    }

    @Override
    public DbImageSpriteMap getParent() {
        return dbImageSpriteMap;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbImageSpriteMapFrame that = (DbImageSpriteMapFrame) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbImageSpriteMapFrame{id=" + id + '}';
    }
}
