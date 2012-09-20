package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemInfo;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.packets.BoxPickedPacket;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryArtifactCount;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */

@Component(value = "inventoryService")
public class GlobalInventoryServiceImpl implements GlobalInventoryService {
    @Autowired
    private CrudRootServiceHelper<DbInventoryArtifact> artifactCrud;
    @Autowired
    private CrudRootServiceHelper<DbInventoryItem> itemCrud;
    @Autowired
    private CrudRootServiceHelper<DbInventoryNewUser> newUserCrud;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerConnectionService serverConnectionService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private Log log = LogFactory.getLog(GlobalInventoryServiceImpl.class);

    @PostConstruct
    public void init() {
        artifactCrud.init(DbInventoryArtifact.class);
        itemCrud.init(DbInventoryItem.class);
        newUserCrud.init(DbInventoryNewUser.class);
    }

    @Override
    public CrudRootServiceHelper<DbInventoryArtifact> getArtifactCrud() {
        return artifactCrud;
    }

    @Override
    public CrudRootServiceHelper<DbInventoryItem> getItemCrud() {
        return itemCrud;
    }

    @Override
    public CrudRootServiceHelper<DbInventoryNewUser> getNewUserCrud() {
        return newUserCrud;
    }

    private void addBoxContentToUser(DbBoxItemTypePossibility dbBoxItemTypePossibility, UserState userState, StringBuilder builder) {
        builder.append("<li>");
        if (dbBoxItemTypePossibility.getDbInventoryItem() != null) {
            userState.addInventoryItem(dbBoxItemTypePossibility.getDbInventoryItem().getId());
            historyService.addInventoryItemFromBox(userState, dbBoxItemTypePossibility.getDbInventoryItem().getName());
            builder.append("Item: ").append(dbBoxItemTypePossibility.getDbInventoryItem().getName());
        } else if (dbBoxItemTypePossibility.getDbInventoryArtifact() != null) {
            userState.addInventoryArtifact(dbBoxItemTypePossibility.getDbInventoryArtifact().getId());
            historyService.addInventoryArtifactFromBox(userState, dbBoxItemTypePossibility.getDbInventoryArtifact().getName());
            builder.append("Artifact: ").append(dbBoxItemTypePossibility.getDbInventoryArtifact().getName());
        } else if (dbBoxItemTypePossibility.getRazarion() != null) {
            userState.addRazarion(dbBoxItemTypePossibility.getRazarion());
            historyService.addRazarionFromBox(userState, dbBoxItemTypePossibility.getRazarion());
            builder.append("Razarion: ").append(dbBoxItemTypePossibility.getRazarion());
        } else {
            log.warn("No content defined for box: " + dbBoxItemTypePossibility.getParent() + " " + dbBoxItemTypePossibility);
        }
        builder.append("</li>");
    }

    @Override
    public void assembleInventoryItem(int inventoryItemId) {
        UserState userState = userService.getUserState();
        DbInventoryItem dbInventoryItem = itemCrud.readDbChild(inventoryItemId);
        if (dbInventoryItem.getArtifactCountCrud().readDbChildren().isEmpty()) {
            throw new IllegalArgumentException("Can not assemble item with no artifacts: " + dbInventoryItem);
        }
        Collection<Integer> artifactIds = new ArrayList<>();
        for (DbInventoryArtifactCount dbInventoryArtifactCount : dbInventoryItem.getArtifactCountCrud().readDbChildren()) {
            for (int count = 0; count < dbInventoryArtifactCount.getCount(); count++) {
                artifactIds.add(dbInventoryArtifactCount.getDbInventoryArtifact().getId());
            }
        }
        if (!userState.removeArtifactIds(artifactIds)) {
            throw new IllegalArgumentException("Can not assemble inventory item: " + dbInventoryItem + " user: " + userState + ". Some inventory artifacts are mission");
        }
        userState.addInventoryItem(dbInventoryItem.getId());
    }

    @Override
    public int buyInventoryItem(int inventoryItemId) {
        DbInventoryItem dbInventoryItem = itemCrud.readDbChild(inventoryItemId);
        UserState userState = userService.getUserState();
        if (dbInventoryItem.getRazarionCoast() == null) {
            throw new IllegalArgumentException("The InventoryItem can not be bought: " + userState + " dbInventoryItem: " + dbInventoryItem);
        }
        if (dbInventoryItem.getRazarionCoast() > userState.getRazarion()) {
            throw new IllegalArgumentException("The user does not have enough razarion to buy the inventory item. User: " + userState + " dbInventoryItem: " + dbInventoryItem + " Razarion: " + userState.getRazarion());
        }
        userState.subRazarion(dbInventoryItem.getRazarionCoast());
        userState.addInventoryItem(dbInventoryItem.getId());
        historyService.addInventoryItemBought(userState, dbInventoryItem.getName(), dbInventoryItem.getRazarionCoast());
        return userState.getRazarion();
    }

    @Override
    public int buyInventoryArtifact(int inventoryArtifactId) {
        DbInventoryArtifact dbInventoryArtifact = artifactCrud.readDbChild(inventoryArtifactId);
        UserState userState = userService.getUserState();
        if (dbInventoryArtifact.getRazarionCoast() == null) {
            throw new IllegalArgumentException("The InventoryArtifact can not be bought: " + userState + " dbInventoryItem: " + dbInventoryArtifact);
        }
        if (dbInventoryArtifact.getRazarionCoast() > userState.getRazarion()) {
            throw new IllegalArgumentException("The user does not have enough razarion to buy the inventory artifact. User: " + userState + " dbInventoryArtifact: " + dbInventoryArtifact + " Razarion: " + userState.getRazarion());
        }
        userState.subRazarion(dbInventoryArtifact.getRazarionCoast());
        userState.addInventoryArtifact(dbInventoryArtifact.getId());
        historyService.addInventoryArtifactBought(userState, dbInventoryArtifact.getName(), dbInventoryArtifact.getRazarionCoast());
        return userState.getRazarion();
    }

    @Override
    public InventoryInfo getInventory() {
        InventoryInfo inventoryInfo = new InventoryInfo();
        UserState userState = userService.getUserState();
        Map<Integer, InventoryArtifactInfo> allArtifacts = getAllInventoryArtifactInfoFromDb();
        Map<Integer, InventoryItemInfo> allItems = getAllInventoryItemsInfoFromDb(allArtifacts);

        // Set razarion
        inventoryInfo.setRazarion(userState.getRazarion());
        // Set all items
        inventoryInfo.setAllInventoryItemInfos(new ArrayList<>(allItems.values())); // new ArrayList() due to GWT problems
        // Set all artifacts
        inventoryInfo.setAllInventoryArtifactInfos(new ArrayList<>(allArtifacts.values())); // new ArrayList() due to GWT problems
        // Set own artifacts
        Map<InventoryArtifactInfo, Integer> ownInventoryArtifacts = new HashMap<>();
        for (Integer artifactId : userState.getInventoryArtifactIds()) {
            InventoryArtifactInfo inventoryArtifactInfo = allArtifacts.get(artifactId);
            if (inventoryArtifactInfo == null) {
                throw new IllegalStateException("InventoryArtifactInfo does not exist: " + artifactId);
            }
            if (ownInventoryArtifacts.containsKey(inventoryArtifactInfo)) {
                continue;
            }
            ownInventoryArtifacts.put(inventoryArtifactInfo, Collections.frequency(userState.getInventoryArtifactIds(), artifactId));
        }
        inventoryInfo.setOwnInventoryArtifacts(ownInventoryArtifacts);
        // Set own items
        Map<InventoryItemInfo, Integer> ownInventoryItems = new HashMap<>();
        for (Integer itemId : userState.getInventoryItemIds()) {
            InventoryItemInfo inventoryItemInfo = allItems.get(itemId);
            if (inventoryItemInfo == null) {
                throw new IllegalStateException("InventoryItemInfo does not exist: " + itemId);
            }
            if (ownInventoryItems.containsKey(inventoryItemInfo)) {
                continue;
            }
            ownInventoryItems.put(inventoryItemInfo, Collections.frequency(userState.getInventoryItemIds(), itemId));
        }
        inventoryInfo.setOwnInventoryItems(ownInventoryItems);

        return inventoryInfo;
    }

    @Override
    public void setupNewUserState(UserState userState) {
        for (DbInventoryNewUser dbInventoryNewUser : newUserCrud.readDbChildren()) {
            if (dbInventoryNewUser.getRazarion() != null) {
                userState.addRazarion(dbInventoryNewUser.getRazarion());
            }
            if (dbInventoryNewUser.getDbInventoryItem() != null) {
                for (int i = 0; i < dbInventoryNewUser.getCount(); i++) {
                    userState.addInventoryItem(dbInventoryNewUser.getDbInventoryItem().getId());
                }
            }
            if (dbInventoryNewUser.getDbInventoryArtifact() != null) {
                for (int i = 0; i < dbInventoryNewUser.getCount(); i++) {
                    userState.addInventoryArtifact(dbInventoryNewUser.getDbInventoryArtifact().getId());
                }
            }
        }
    }

    private Map<Integer, InventoryArtifactInfo> getAllInventoryArtifactInfoFromDb() {
        Map<Integer, InventoryArtifactInfo> result = new HashMap<>();
        for (DbInventoryArtifact dbInventoryArtifact : artifactCrud.readDbChildren()) {
            result.put(dbInventoryArtifact.getId(), dbInventoryArtifact.generateInventoryArtifactInfo());
        }
        return result;
    }

    private Map<Integer, InventoryItemInfo> getAllInventoryItemsInfoFromDb(Map<Integer, InventoryArtifactInfo> allArtifacts) {
        Map<Integer, InventoryItemInfo> result = new HashMap<>();
        for (DbInventoryItem dbInventoryItem : itemCrud.readDbChildren()) {
            result.put(dbInventoryItem.getId(), dbInventoryItem.generateInventoryItemInfo(allArtifacts));
        }
        return result;
    }

    @Override
    public void onSyncBoxItemPicked(SyncBoxItem box, SyncBaseItem picker) {
        UserState userState = planetSystemService.getServerPlanetServices(picker.getBase()).getBaseService().getUserState(picker.getBase());
        StringBuilder builder = new StringBuilder();
        builder.append("You picked up a box! Items added to your Inventory:");
        builder.append("<ul>");
        boolean somethingAdded = false;
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            historyService.addBoxPicked(box, picker);
            DbBoxItemType dbBoxItemType = serverItemTypeService.getDbBoxItemType(box.getItemType().getId());
            for (DbBoxItemTypePossibility dbBoxItemTypePossibility : dbBoxItemType.getBoxPossibilityCrud().readDbChildren()) {
                if (MathHelper.isRandomPossibility(dbBoxItemTypePossibility.getPossibility())) {
                    addBoxContentToUser(dbBoxItemTypePossibility, userState, builder);
                    somethingAdded = true;
                }
            }
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
        if (!somethingAdded) {
            builder.append("<li>No luck. Empty box found</li>");
        }
        builder.append("</ul>");
        BoxPickedPacket boxPickedPacket = new BoxPickedPacket();
        boxPickedPacket.setHtml(builder.toString());
        serverConnectionService.sendPacket(picker.getBase(), boxPickedPacket);
    }

    @Override
    public BoxItemType getDropBox4ItemType(BaseItemType baseItemType) {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            DbBaseItemType dbBaseItemType = serverItemTypeService.getDbBaseItemType(baseItemType.getId());
            if(dbBaseItemType.getDbBoxItemType() == null) {
                return null;
            }
            return (BoxItemType) serverItemTypeService.getItemType(dbBaseItemType.getDbBoxItemType());
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        UserState userState = userService.getUserState();
        Base base = userState.getBase();
        if (base == null) {
            throw new IllegalStateException("User does not have a base: " + userState);
        }
        DbInventoryItem dbInventoryItem = getItemCrud().readDbChild(inventoryItemId);
        if (!userState.hasInventoryItemId(inventoryItemId)) {
            throw new IllegalArgumentException("User does not have inventory item: " + dbInventoryItem + " user: " + userState);
        }
        planetSystemService.useInventoryItem(userState, base.getSimpleBase(), dbInventoryItem, positionToBePlaced);

        userState.removeInventoryItemId(inventoryItemId);
        historyService.addInventoryItemUsed(userState, dbInventoryItem.getName());
    }

}
