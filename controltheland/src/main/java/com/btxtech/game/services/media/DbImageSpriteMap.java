package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 23:02
 */
@Entity(name = "IMAGE_SPRITE_MAP_LIBRARY")
public class DbImageSpriteMap implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private int frameCount;
    private int frameWidth;
    private int frameHeight;
    private int frameTime;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dbImageSpriteMap", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbImageSpriteMapFrame> imageSpriteMapFrames;
    @Transient
    private CrudListChildServiceHelper<DbImageSpriteMapFrame> imageSpriteMapFrameCrud;

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
        imageSpriteMapFrames = new ArrayList<>();
    }

    @Override
    public void setParent(Object o) {
        // Ignore
    }

    @Override
    public Object getParent() {
        return null;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public int getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(int frameTime) {
        this.frameTime = frameTime;
    }

    public List<DbImageSpriteMapFrame> getImageSpriteMapFrames() {
        return imageSpriteMapFrames;
    }

    public void fillClipInfo(ClipInfo clipInfo) {
        clipInfo.setSpriteMapId(id);
        clipInfo.setFrameCount(frameCount);
        clipInfo.setFrameWidth(frameWidth);
        clipInfo.setFrameHeight(frameHeight);
        clipInfo.setFrameTime(frameTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbImageSpriteMap that = (DbImageSpriteMap) o;

        return id != null && !id.equals(that.id);
    }

    public CrudListChildServiceHelper<DbImageSpriteMapFrame> getImageSpriteMapFrameCrud() {
        if (imageSpriteMapFrameCrud == null) {
            imageSpriteMapFrameCrud = new CrudListChildServiceHelper<>(imageSpriteMapFrames, DbImageSpriteMapFrame.class, this);
        }
        return imageSpriteMapFrameCrud;
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbImageSpriteMap{id=" + id + '}';
    }
}
