package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.statistics.StatisticsEntry;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.condition.DbGenericComparisonValue;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.LocaleType;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * User: beat
 * Date: 30.03.2011
 * Time: 21:49:19
 */
@Entity(name = "BACKUP_USER_STATUS")
@TypeDef(name = "locale", typeClass = LocaleType.class)
public class DbUserState {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer userId;
    @OneToOne(fetch = FetchType.LAZY)
    private DbBase base;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbLevel currentLevel;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DbBackupEntry backupEntry;
    private int xp;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_LEVEL_TASK_DONE",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "levelTask")})
    private Collection<DbLevelTask> levelTasksDone;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbLevelTask activeQuest;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbUserState")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbGenericComparisonValue> dbGenericComparisonValues;
    @Embedded
    private StatisticsEntry statisticsEntry;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_INVENTORY_ITEM",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "inventoryItem")})
    private Collection<DbInventoryItem> inventoryItems;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_INVENTORY_ARTIFACT",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "inventoryArtifact")})
    private Collection<DbInventoryArtifact> inventoryArtifacts;
    private int razarion;
    @org.hibernate.annotations.Type(type = "locale")
    private Locale locale;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_UNLOCKED_ITEM",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "unlockedItem")})
    private Collection<DbBaseItemType> unlockedItemTypes;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_UNLOCKED_QUESTS",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "unlockedQuests")})
    private Collection<DbLevelTask> unlockedQuests;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_UNLOCKED_PLANETS",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "unlockedPlanets")})
    private Collection<DbPlanet> unlockedPlanets;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userState", fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbStorablePacket> storablePackets;


    /**
     * Used by hibernate
     */
    protected DbUserState() {
    }

    public DbUserState(DbBackupEntry backupEntry, User user, UserState userState, DbLevel dbLevel, Collection<DbInventoryItem> inventoryItems, Collection<DbInventoryArtifact> inventoryArtifacts, Collection<DbBaseItemType> unlockedItemTypes, Collection<DbLevelTask> unlockedQuests, Collection<DbPlanet> unlockedPlanets) {
        this.backupEntry = backupEntry;
        if (user != null) {
            this.userId = user.getId();
        }
        xp = userState.getXp();
        razarion = userState.getRazarion();
        currentLevel = dbLevel;
        this.inventoryItems = inventoryItems;
        this.inventoryArtifacts = inventoryArtifacts;
        locale = userState.getLocale();
        this.unlockedItemTypes = unlockedItemTypes;
        this.unlockedQuests = unlockedQuests;
        this.unlockedPlanets = unlockedPlanets;
    }

    public UserState createUserState(UserService userService) {
        UserState userState = new UserState();
        try {
            User realUser = userService.getUser(userId);
            if (realUser != null) {
                userState.setUser(userId);
            } else {
                LogFactory.getLog(DbUserState.class).warn("DbUserState.createUserState() user does not exist any longer: " + userId);
                return null;
            }
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
            return null;
        }
        userState.setXp(xp);
        userState.setRazarion(razarion);
        userState.setLocale(locale);
        if (currentLevel != null) {
            userState.setDbLevelId(currentLevel.getId());
        }
        for (DbInventoryItem inventoryItem : inventoryItems) {
            userState.addInventoryItem(inventoryItem.getId());
        }
        for (DbInventoryArtifact inventoryArtifact : inventoryArtifacts) {
            userState.addInventoryArtifact(inventoryArtifact.getId());
        }
        for (DbStorablePacket dbStorablePacket : storablePackets) {
            userState.saveStorablePackage(dbStorablePacket.createStorablePacket());
        }
        return userState;
    }

    public DbBase getBase() {
        return base;
    }

    public void setBase(DbBase base) {
        this.base = base;
    }

    public void addDbGenericComparisonValue(DbGenericComparisonValue dbGenericComparisonValue) {
        if (dbGenericComparisonValues == null) {
            dbGenericComparisonValues = new ArrayList<>();
        }
        dbGenericComparisonValues.add(dbGenericComparisonValue);
    }

    public Collection<DbGenericComparisonValue> getDbGenericComparisonValues() {
        return dbGenericComparisonValues;
    }

    public Collection<DbLevelTask> getLevelTasksDone() {
        return levelTasksDone;
    }

    public void setLevelTasksDone(Collection<DbLevelTask> levelTasksDone) {
        this.levelTasksDone = levelTasksDone;
    }

    public DbLevelTask getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(DbLevelTask activeQuest) {
        this.activeQuest = activeQuest;
    }

    public StatisticsEntry getStatisticsEntry() {
        return statisticsEntry;
    }

    public void setStatisticsEntry(StatisticsEntry statisticsEntry) {
        this.statisticsEntry = statisticsEntry;
    }

    public Collection<DbBaseItemType> getUnlockedItemTypes() {
        return unlockedItemTypes;
    }

    public Collection<DbLevelTask> getUnlockedQuests() {
        return unlockedQuests;
    }

    public Collection<DbPlanet> getUnlockedPlanets() {
        return unlockedPlanets;
    }

    public Collection<DbStorablePacket> getStorablePackets() {
        return storablePackets;
    }

    public void setStorablePackets(Collection<DbStorablePacket> storablePackets) {
        this.storablePackets = storablePackets;
    }

    @Override
    public String toString() {
        return "DbUserState: userId=" + userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbUserState)) return false;

        DbUserState userState = (DbUserState) o;

        return id != null && id.equals(userState.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
