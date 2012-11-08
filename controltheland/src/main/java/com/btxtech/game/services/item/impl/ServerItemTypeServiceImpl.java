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

package com.btxtech.game.services.item.impl;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemTypeService;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.user.SecurityRoles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: Jun 3, 2009
 * Time: 12:59:07 PM
 */
@Component(value = "serverItemTypeService")
public class ServerItemTypeServiceImpl extends AbstractItemTypeService implements ServerItemTypeService {
    @Autowired
    private CrudRootServiceHelper<DbItemType> dbItemTypeCrud;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ClipService clipService;
    private Log log = LogFactory.getLog(ServerItemTypeServiceImpl.class);
    private HashMap<Integer, ImageHolder> itemTypeSpriteMaps = new HashMap<>();

    @PostConstruct
    public void setup() {
        dbItemTypeCrud.init(DbItemType.class);
        try {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            activate();
        } catch (Throwable t) {
            log.error("", t);        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbItemTypes(Collection<DbItemType> itemTypes) {
        HibernateUtil.saveOrUpdateAll(sessionFactory, itemTypes);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveAttackMatrix(Collection<DbBaseItemType> weaponDbItemTypes) {
        for (DbBaseItemType weaponDbItemType : weaponDbItemTypes) {
            sessionFactory.getCurrentSession().merge(weaponDbItemType);
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbItemType(DbItemType dbItemType) {
        sessionFactory.getCurrentSession().saveOrUpdate(dbItemType);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveItemTypeProperties(int itemTypeId, BoundingBox boundingBox, ItemTypeSpriteMap itemTypeSpriteMap, WeaponType weaponType, Collection<ItemTypeImageInfo> buildupImages, Collection<ItemTypeImageInfo> runtimeImages, Collection<ItemTypeImageInfo> demolitionImages) throws NoSuchItemTypeException {
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        dbItemType.setBounding(boundingBox);
        dbItemType.setTypeSpriteMap(itemTypeSpriteMap, clipService);
        if (dbItemType instanceof DbBaseItemType && ((DbBaseItemType) dbItemType).getDbWeaponType() != null) {
            saveWeaponType(dbItemType, weaponType);
        }
        dbItemType.saveImages(buildupImages, runtimeImages, demolitionImages);
        saveDbItemType(dbItemType);
    }

    private void saveWeaponType(DbItemType dbItemType, WeaponType weaponType) throws NoSuchItemTypeException {
        if (!(dbItemType instanceof DbBaseItemType)) {
            throw new IllegalArgumentException("Given item type is not instance of a DbBaseItemType: " + dbItemType);
        }

        DbBaseItemType dbBaseItemType = (DbBaseItemType) dbItemType;
        if (dbBaseItemType.getDbWeaponType() == null) {
            throw new IllegalArgumentException("Given item type has no DbWeaponType: " + dbItemType);
        }

        dbBaseItemType.getDbWeaponType().setMuzzleFlashPositions(weaponType.getMuzzleFlashPositions());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbItemType> getDbItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbItemType.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbBaseItemType> getDbBaseItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBaseItemType.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbProjectileItemType> getDbProjectileItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbProjectileItemType.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbBaseItemType> getWeaponDbBaseItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBaseItemType.class);
        criteria.add(Restrictions.isNotNull("dbWeaponType"));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public void activate() {
        Collection<DbItemType> dbItemTypes = getDbItemTypes();
        ArrayList<ItemType> itemTypes = new ArrayList<>();
        itemTypeSpriteMaps.clear();
        for (DbItemType dbItemType : dbItemTypes) {
            try {
                ItemType itemType = dbItemType.createItemType();
                itemTypes.add(itemType);
                addItemTypeImages(dbItemType, itemType);
            } catch (RuntimeException e) {
                log.error("Can not activate item type: " + dbItemType.getName() + " id: " + dbItemType.getId());
                throw e;
            }
        }
        synchronize(itemTypes);
    }

    private void synchronize(Collection<ItemType> itemTypes) {
        ArrayList<ItemType> newItems = new ArrayList<>(itemTypes);
        newItems.removeAll(getItemTypes());
        ArrayList<ItemType> removedItems = new ArrayList<>(getItemTypes());
        removedItems.removeAll(itemTypes);
        ArrayList<ItemType> changingItems = new ArrayList<>(itemTypes);
        changingItems.retainAll(getItemTypes());

        putAll(newItems);
        removeAll(removedItems);
        changeAll(changingItems);
    }

    private void addItemTypeImages(DbItemType dbItemType, ItemType itemType) {
        try {
            List<DbItemTypeImage> allImages = new ArrayList<>(dbItemType.getItemTypeImageCrud().readDbChildren());
            List<DbItemTypeImage> buildup = new ArrayList<>();
            List<DbItemTypeImage> runtime = new ArrayList<>();
            List<DbItemTypeImage> demolition = new ArrayList<>();
            DbItemTypeImage exampleImage = null;

            for (DbItemTypeImage image : allImages) {
                if (exampleImage == null && image.getData() != null) {
                    exampleImage = image;
                }
                if (image.getType() == null) {
                    log.warn("ServerItemTypeServiceImpl.addSpriteMapItemTypeImage() type in null: " + image.getType() + " id: " + image.getId());
                    continue;
                }
                switch (image.getType()) {
                    case BUILD_UP:
                        buildup.add(image);
                        break;
                    case RUN_TIME:
                        runtime.add(image);
                        break;
                    case DEMOLITION:
                        demolition.add(image);
                        break;
                    default:
                        log.warn("ServerItemTypeServiceImpl.addSpriteMapItemTypeImage() unknown type in DbItemType: " + image.getType() + " id: " + image.getId());
                }
            }

            if (exampleImage == null) {
                log.warn("ServerItemTypeServiceImpl.addSpriteMapItemTypeImage() not valid item type image for: " + dbItemType.getName() + " " + dbItemType.getId());
                return;
            }

            Collections.sort(buildup, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    if (o1.getStep() != o2.getStep()) {
                        return Integer.compare(o1.getStep(), o2.getStep());
                    } else {
                        return Integer.compare(o1.getFrame(), o2.getFrame());
                    }
                }
            });
            Collections.sort(runtime, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    if (o1.getAngelIndex() != o2.getAngelIndex()) {
                        return Integer.compare(o1.getAngelIndex(), o2.getAngelIndex());
                    } else {
                        return Integer.compare(o1.getFrame(), o2.getFrame());
                    }
                }
            });
            Collections.sort(demolition, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    if (o1.getAngelIndex() != o2.getAngelIndex()) {
                        return Integer.compare(o1.getAngelIndex(), o2.getAngelIndex());
                    } else {
                        if (o1.getStep() != o2.getStep()) {
                            return Integer.compare(o1.getStep(), o2.getStep());
                        } else {
                            return Integer.compare(o1.getFrame(), o2.getFrame());
                        }
                    }
                }
            });
            BufferedImage masterImage = ImageIO.read(new ByteArrayInputStream(exampleImage.getData()));

            // Get the format name
            Iterator<ImageReader> iter = ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(exampleImage.getData())));
            if (!iter.hasNext()) {
                throw new IllegalArgumentException("Can not find image reader: " + dbItemType);
            }
            String formatName = iter.next().getFormatName();

            ItemTypeSpriteMap itemTypeSpriteMap = itemType.getItemTypeSpriteMap();
            int totalImageCount = itemTypeSpriteMap.getBuildupSteps() * itemTypeSpriteMap.getBuildupAnimationFrames();
            totalImageCount += itemType.getBoundingBox().getAngelCount() * itemTypeSpriteMap.getRuntimeAnimationFrames();
            totalImageCount += itemType.getBoundingBox().getAngelCount() * itemTypeSpriteMap.getDemolitionFramesPerAngel();
            BufferedImage spriteMap = new BufferedImage(dbItemType.getImageWidth() * totalImageCount, dbItemType.getImageHeight(), masterImage.getType());
            int xPos = 0;
            String contentType = exampleImage.getContentType();
            for (DbItemTypeImage dbItemTypeImage : buildup) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbItemTypeImage.getData()));
                boolean done = spriteMap.createGraphics().drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("Buildup image could not be drawn: " + dbItemType + " image number: " + dbItemTypeImage.getId());
                }
                xPos += dbItemType.getImageWidth();
            }
            for (DbItemTypeImage dbItemTypeImage : runtime) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbItemTypeImage.getData()));
                boolean done = spriteMap.createGraphics().drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("Runtime image could not be drawn: " + dbItemType + " image number: " + dbItemTypeImage.getId());
                }
                xPos += dbItemType.getImageWidth();
            }
            for (DbItemTypeImage dbItemTypeImage : demolition) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbItemTypeImage.getData()));
                boolean done = spriteMap.createGraphics().drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("Demolition image could not be drawn: " + dbItemType + " image number: " + dbItemTypeImage.getId());
                }
                xPos += dbItemType.getImageWidth();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(spriteMap, formatName, outputStream);
            itemTypeSpriteMaps.put(dbItemType.getId(), new ImageHolder(outputStream.toByteArray(), contentType));
        } catch (Exception e) {
            log.error("ServerItemTypeServiceImpl.addSpriteMapItemTypeImage() error with DbItemType: " + dbItemType, e);
        }
    }

    @Override
    public DbItemType getDbItemType(int itemTypeId) {
        return HibernateUtil.get(sessionFactory, DbItemType.class, itemTypeId);
    }

    @Override
    public DbBaseItemType getDbBaseItemType(int itemBaseTypeId) {
        return HibernateUtil.get(sessionFactory, DbBaseItemType.class, itemBaseTypeId);
    }

    @Override
    public DbResourceItemType getDbResourceItemType(int resourceItemType) {
        return HibernateUtil.get(sessionFactory, DbResourceItemType.class, resourceItemType);
    }

    @Override
    public DbBoxItemType getDbBoxItemType(int boxItemType) {
        return HibernateUtil.get(sessionFactory, DbBoxItemType.class, boxItemType);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
    public void deleteItemType(DbItemType dbItemType) {
        sessionFactory.getCurrentSession().delete(dbItemType);
    }

    @Override
    public DbItemTypeImage getCmsDbItemTypeImage(int itemTypeId) throws NoSuchItemTypeException {
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        ItemType itemType = getItemType(itemTypeId);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbItemTypeImage.class);
        criteria.add(Restrictions.eq("itemType", dbItemType));
        criteria.add(Restrictions.eq("type", ItemTypeSpriteMap.SyncObjectState.RUN_TIME));
        criteria.add(Restrictions.eq("angelIndex", itemType.getBoundingBox().getCosmeticAngelIndex()));
        criteria.add(Restrictions.eq("frame", 0));
        criteria.add(Restrictions.eq("step", 0));
        List images = criteria.list();
        if (images.size() != 1) {
            throw new IllegalStateException("Wrong item type image count for: " + dbItemType + " received: " + images.size());
        }
        return (DbItemTypeImage) images.get(0);
    }

    @Override
    public ImageHolder getItemTypeSpriteMap(int itemTypeId) {
        ImageHolder imageHolder = itemTypeSpriteMaps.get(itemTypeId);
        if (imageHolder == null) {
            throw new IllegalArgumentException("Sprite map for item type id does not exist: " + itemTypeId);
        }
        return imageHolder;
    }

    @Override
    public ItemType getItemType(DbItemType dbItemType) {
        ItemType itemType;
        try {
            itemType = getItemType(dbItemType.getId());
        } catch (NoSuchItemTypeException e) {
            throw new RuntimeException(e);
        }
        return itemType;
    }

    @Override
    public CrudRootServiceHelper<DbItemType> getDbItemTypeCrud() {
        return dbItemTypeCrud;
    }
}
