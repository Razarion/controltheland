package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.11.2011
 * Time: 01:41:03
 */
@Entity(name = "TERRAIN_IMAGE_GROUP")
public class DbTerrainImageGroup implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbTerrainImage> dbTerrainImages;
    private String htmlBackgroundColorNone;
    private String htmlBackgroundColorWater;
    private String htmlBackgroundColorLand;
    private String htmlBackgroundColorLandCoast;
    private String htmlBackgroundColorWaterCoast;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbImageSpriteMap imageSpriteMap;
    @Transient
    private CrudChildServiceHelper<DbTerrainImage> terrainImageCrud;

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

    public String getHtmlBackgroundColorNone() {
        return htmlBackgroundColorNone;
    }

    public void setHtmlBackgroundColorNone(String htmlBackgroundColorNone) {
        this.htmlBackgroundColorNone = htmlBackgroundColorNone;
    }

    public String getHtmlBackgroundColorWater() {
        return htmlBackgroundColorWater;
    }

    public void setHtmlBackgroundColorWater(String htmlBackgroundColorWater) {
        this.htmlBackgroundColorWater = htmlBackgroundColorWater;
    }

    public String getHtmlBackgroundColorLand() {
        return htmlBackgroundColorLand;
    }

    public void setHtmlBackgroundColorLand(String htmlBackgroundColorLand) {
        this.htmlBackgroundColorLand = htmlBackgroundColorLand;
    }

    public String getHtmlBackgroundColorLandCoast() {
        return htmlBackgroundColorLandCoast;
    }

    public void setHtmlBackgroundColorLandCoast(String htmlBackgroundColorLandCoast) {
        this.htmlBackgroundColorLandCoast = htmlBackgroundColorLandCoast;
    }

    public String getHtmlBackgroundColorWaterCoast() {
        return htmlBackgroundColorWaterCoast;
    }

    public void setHtmlBackgroundColorWaterCoast(String htmlBackgroundColorWaterCoast) {
        this.htmlBackgroundColorWaterCoast = htmlBackgroundColorWaterCoast;
    }

    public DbImageSpriteMap getImageSpriteMap() {
        return imageSpriteMap;
    }

    public void setImageSpriteMap(DbImageSpriteMap imageSpriteMap) {
        this.imageSpriteMap = imageSpriteMap;
    }

    @Override
    public void init(UserService userService) {
        dbTerrainImages = new ArrayList<>();
        htmlBackgroundColorNone = "#FFFFFF";
        htmlBackgroundColorWater = "#FFFFFF";
        htmlBackgroundColorLand = "#FFFFFF";
        htmlBackgroundColorLandCoast = "#FFFFFF";
        htmlBackgroundColorWaterCoast = "#FFFFFF";
    }

    public CrudChildServiceHelper<DbTerrainImage> getTerrainImageCrud() {
        if (terrainImageCrud == null) {
            terrainImageCrud = new CrudChildServiceHelper<>(dbTerrainImages, DbTerrainImage.class, this);
        }
        return terrainImageCrud;
    }

    public ImageSpriteMapInfo createImageSpriteMapInfo() {
        if (imageSpriteMap != null) {
            return imageSpriteMap.createImageSpriteMapInfo();
        } else {
            return null;
        }
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTerrainImageGroup that = (DbTerrainImageGroup) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
