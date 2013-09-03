package com.btxtech.game.services.media.impl;

import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbClip;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.media.DbImageSpriteMapFrame;
import com.btxtech.game.services.media.PreloadedImageSpriteMap;
import com.btxtech.game.services.user.SecurityRoles;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 18:45
 */
@Component(value = "clipService")
public class ClipServiceImpl implements ClipService {
    @Autowired
    private CrudRootServiceHelper<DbClip> clipLibraryCrud;
    @Autowired
    private CrudRootServiceHelper<PreloadedImageSpriteMap> preloadedSpriteMapCrud;
    @Autowired
    private CrudRootServiceHelper<DbImageSpriteMap> imageSpriteMapCrud;
    @Autowired
    private SessionFactory sessionFactory;
    private final Map<Integer, ImageHolder> imageSpriteMapCache = new HashMap<>();

    @PostConstruct
    public void setup() {
        clipLibraryCrud.init(DbClip.class);
        preloadedSpriteMapCrud.init(PreloadedImageSpriteMap.class);
        imageSpriteMapCrud.init(DbImageSpriteMap.class);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            activateImageSpriteMapCache();
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public CrudRootServiceHelper<DbClip> getClipLibraryCrud() {
        return clipLibraryCrud;
    }

    @Override
    public CrudRootServiceHelper<PreloadedImageSpriteMap> getPreloadedSpriteMapCrud() {
        return preloadedSpriteMapCrud;
    }

    @Override
    public CrudRootServiceHelper<DbImageSpriteMap> getImageSpriteMapCrud() {
        return imageSpriteMapCrud;
    }

    @Override
    public Collection<ImageSpriteMapInfo> getImageSpriteMapLibrary() {
        Collection<ImageSpriteMapInfo> imageSpriteMapInfos = new ArrayList<>();
        for (DbImageSpriteMap imageSpriteMap : imageSpriteMapCrud.readDbChildren()) {
            imageSpriteMapInfos.add(imageSpriteMap.createImageSpriteMapInfo());
        }
        return imageSpriteMapInfos;
    }

    @Override
    public Collection<ClipInfo> getClipLibrary() {
        Collection<ClipInfo> clipLibrary = new ArrayList<>();
        for (DbClip dbClip : clipLibraryCrud.readDbChildren()) {
            clipLibrary.add(dbClip.createClipInfo());
        }
        return clipLibrary;
    }

    @Override
    public PreloadedImageSpriteMapInfo getPreloadedImageSpriteMapInfo() {
        PreloadedImageSpriteMapInfo preloadedImageSpriteMapInfo = new PreloadedImageSpriteMapInfo();
        for (PreloadedImageSpriteMap preloadedImageSpriteMap : preloadedSpriteMapCrud.readDbChildren()) {
            try {
                preloadedImageSpriteMapInfo.add(preloadedImageSpriteMap.getType(), preloadedImageSpriteMap.getDbImageSpriteMap().getId());
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
        }
        return preloadedImageSpriteMapInfo;
    }

    @Override
    public ImageHolder getImageSpriteMap(int imageSpriteMapId) {
        synchronized (imageSpriteMapCache) {
            ImageHolder imageHolder = imageSpriteMapCache.get(imageSpriteMapId);
            if (imageHolder == null) {
                throw new IllegalArgumentException("No image sprite map for id: " + imageSpriteMapId);
            }
            return imageHolder;
        }
    }

    @Override
    public void activateImageSpriteMapCache() {
        Map<Integer, ImageHolder> tmpMap = new HashMap<>();
        for (DbImageSpriteMap dbImageSpriteMap : imageSpriteMapCrud.readDbChildren()) {
            try {
                tmpMap.put(dbImageSpriteMap.getId(), setupSpriteMap(dbImageSpriteMap));
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
        }

        synchronized (imageSpriteMapCache) {
            imageSpriteMapCache.clear();
            imageSpriteMapCache.putAll(tmpMap);
        }
    }

    private ImageHolder setupSpriteMap(DbImageSpriteMap dbImageSpriteMap) throws IOException {
        byte[] masterImageData = dbImageSpriteMap.getImageSpriteMapFrames().get(0).getData();
        Iterator<ImageReader> iter = ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(masterImageData)));
        // Get the format name
        if (!iter.hasNext()) {
            throw new IllegalArgumentException("Can not find image reader: " + dbImageSpriteMap);
        }
        ImageReader imageReader = iter.next();
        BufferedImage masterImage = ImageIO.read(new ByteArrayInputStream(masterImageData));
        BufferedImage spriteMap = new BufferedImage(dbImageSpriteMap.getFrameWidth() * dbImageSpriteMap.getImageSpriteMapFrames().size(), dbImageSpriteMap.getFrameHeight(), masterImage.getType());
        int xPos = 0;
        Graphics2D graphics2D = spriteMap.createGraphics();
        for (DbImageSpriteMapFrame dbImageSpriteMapFrame : dbImageSpriteMap.getImageSpriteMapFrames()) {
            if (dbImageSpriteMapFrame.getData() != null && dbImageSpriteMapFrame.getData().length > 0) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbImageSpriteMapFrame.getData()));
                boolean done = graphics2D.drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("setupSpriteMApAndAddToCache() image could not be drawn. dbImageSpriteMap: " + dbImageSpriteMap + " dbImageSpriteMapFrame: " + dbImageSpriteMapFrame);
                }
            }
            xPos += dbImageSpriteMap.getFrameWidth();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(spriteMap, imageReader.getFormatName(), outputStream);
        return new ImageHolder(outputStream.toByteArray(), imageReader.getOriginatingProvider().getMIMETypes()[0]);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveImageSpriteMap(ImageSpriteMapInfo imageSpriteMapInfo, String[] overriddenImages) {
        DbImageSpriteMap dbImageSpriteMap = imageSpriteMapCrud.readDbChild(imageSpriteMapInfo.getId());
        dbImageSpriteMap.setFrameCount(overriddenImages.length);
        dbImageSpriteMap.setFrameWidth(imageSpriteMapInfo.getFrameWidth());
        dbImageSpriteMap.setFrameHeight(imageSpriteMapInfo.getFrameHeight());
        dbImageSpriteMap.setFrameTime(imageSpriteMapInfo.getFrameTime());
        dbImageSpriteMap.setFrames(overriddenImages);
        imageSpriteMapCrud.updateDbChild(dbImageSpriteMap);
    }
}
