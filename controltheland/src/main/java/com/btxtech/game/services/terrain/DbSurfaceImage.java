/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.terrain.ScatterSurfaceImageInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.SpriteMapAssembler;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * User: beat
 * Date: 19.04.2010
 * Time: 22:09:11
 */
@Entity(name = "TERRAIN_SURFACE_IMAGE")
public class DbSurfaceImage implements CrudChild, CrudParent, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 500000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;
    private String contentType;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbImageSpriteMap imageSpriteMap;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbScatterSurfaceImage> scatterSurfaceImages;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SurfaceType surfaceType;
    private String htmlBackgroundColor;
    private double uncommon;
    private double rare;
    @Transient
    private byte[] cachedImageData;
    @Transient
    private String cachedContentType;
    @Transient
    private CrudChildServiceHelper<DbScatterSurfaceImage> scatterSurfaceImageCrudHelper;

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
        surfaceType = SurfaceType.LAND;
        htmlBackgroundColor = "#FFFFFF";
        scatterSurfaceImages = new ArrayList<>();
        uncommon = 0.1;
        rare = 0.01;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public DbImageSpriteMap getImageSpriteMap() {
        return imageSpriteMap;
    }

    public void setImageSpriteMap(DbImageSpriteMap imageSpriteMap) {
        this.imageSpriteMap = imageSpriteMap;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    public double getUncommon() {
        return uncommon;
    }

    public void setUncommon(double uncommon) {
        this.uncommon = uncommon;
    }

    public double getRare() {
        return rare;
    }

    public void setRare(double rare) {
        this.rare = rare;
    }

    public SurfaceImage createSurfaceImage() {
        ImageSpriteMapInfo imageSpriteMapInfo = null;
        if (imageSpriteMap != null) {
            imageSpriteMapInfo = imageSpriteMap.createImageSpriteMapInfo();
        }
        return new SurfaceImage(surfaceType, id, imageSpriteMapInfo, setupScatterSurfaceImageInfo(), htmlBackgroundColor);
    }

    private ScatterSurfaceImageInfo setupScatterSurfaceImageInfo() {
        if (scatterSurfaceImages == null || scatterSurfaceImages.isEmpty()) {
            return null;
        }
        List<DbScatterSurfaceImage> sortedScatterSurfaceImages = new ArrayList<>(scatterSurfaceImages);
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = new ScatterSurfaceImageInfo();
        scatterSurfaceImageInfo.setUncommon(uncommon);
        scatterSurfaceImageInfo.setRare(rare);
        Collections.sort(sortedScatterSurfaceImages);
        int commonCount = 0;
        int uncommonCount = 0;
        int rareCount = 0;
        for (DbScatterSurfaceImage scatterSurfaceImage : sortedScatterSurfaceImages) {
            switch (scatterSurfaceImage.getFrequency()) {
                case COMMON:
                    commonCount++;
                    break;
                case UNCOMMON:
                    uncommonCount++;
                    break;
                case RARE:
                    rareCount++;
                    break;
            }
        }
        scatterSurfaceImageInfo.setCommonImageCount(commonCount);
        scatterSurfaceImageInfo.setUncommonImageCount(uncommonCount);
        scatterSurfaceImageInfo.setRareImageCount(rareCount);
        return scatterSurfaceImageInfo;
    }

    public String getHtmlBackgroundColor() {
        return htmlBackgroundColor;
    }

    public void setHtmlBackgroundColor(String htmlBackgroundColor) {
        this.htmlBackgroundColor = htmlBackgroundColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSurfaceImage that = (DbSurfaceImage) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    public String getCachedContentType() {
        return cachedContentType;
    }

    public byte[] getCachedImageData() {
        return cachedImageData;
    }

    public void cacheImage() throws IOException {
        if (imageSpriteMap != null) {
            cachedImageData = null;
            cachedContentType = null;
        } else if (scatterSurfaceImages != null && !scatterSurfaceImages.isEmpty()) {
            SpriteMapAssembler spriteMapAssembler = setupScatterSpriteMap();
            cachedImageData = spriteMapAssembler.assemble();
            cachedContentType = spriteMapAssembler.getMimeType();
        } else {
            cachedImageData = imageData;
            cachedContentType = contentType;
        }
    }

    private SpriteMapAssembler setupScatterSpriteMap() throws IOException {
        SpriteMapAssembler spriteMapAssembler = new SpriteMapAssembler(scatterSurfaceImages.size(), CommonJava.getFirst(scatterSurfaceImages).getImageData());
        List<DbScatterSurfaceImage> sortedScatterSurfaceImages = new ArrayList<>(scatterSurfaceImages);
        Collections.sort(sortedScatterSurfaceImages);
        for (DbScatterSurfaceImage scatterSurfaceImage : sortedScatterSurfaceImages) {
            spriteMapAssembler.appendImage(scatterSurfaceImage.getImageData());
        }
        return spriteMapAssembler;
    }

    public CrudChildServiceHelper<DbScatterSurfaceImage> getScatterSurfaceImageCrudHelper() {
        if (scatterSurfaceImageCrudHelper == null) {
            scatterSurfaceImageCrudHelper = new CrudChildServiceHelper<>(scatterSurfaceImages, DbScatterSurfaceImage.class, this);
        }
        return scatterSurfaceImageCrudHelper;
    }
}