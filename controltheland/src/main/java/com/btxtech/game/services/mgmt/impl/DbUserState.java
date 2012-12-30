package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.statistics.StatisticsEntry;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.condition.DbGenericComparisonValue;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

/**
 * User: beat
 * Date: 30.03.2011
 * Time: 21:49:19
 */
@Entity(name = "BACKUP_USER_STATUS")
public class DbUserState {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "user_name")
    private String user;
    @OneToOne(fetch = FetchType.LAZY)
    private DbBase base;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbLevel currentLevel;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BackupEntry backupEntry;
    private int xp;
    private boolean sendResurrectionMessage;
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

    /**
     * Used by hibernate
     */
    protected DbUserState() {
    }

    public DbUserState(BackupEntry backupEntry, User user, UserState userState, DbLevel dbLevel, Collection<DbInventoryItem> inventoryItems, Collection<DbInventoryArtifact> inventoryArtifacts) {
        this.backupEntry = backupEntry;
        if (user != null) {
            this.user = user.getUsername();
        }
        xp = userState.getXp();
        razarion = userState.getRazarion();
        currentLevel = dbLevel;
        sendResurrectionMessage = userState.isSendResurrectionMessage();
        this.inventoryItems = inventoryItems;
        this.inventoryArtifacts = inventoryArtifacts;
    }

    public UserState createUserState(UserService userService) {
        UserState userState = new UserState();
        if (user != null) {
            try {
                User realUser = userService.getUser(user);
                if (realUser != null) {
                    userState.setUser(realUser.getUsername());
                } else {
                    LogFactory.getLog(DbUserState.class).warn("DbUserState.createUserState() user does not exist any longer: " + user);
                    return null;
                }
            } catch (Throwable t) {
                ExceptionHandler.handleException(t);
                return null;
            }
        }
        userState.setXp(xp);
        userState.setRazarion(razarion);
        if (sendResurrectionMessage) {
            userState.setSendResurrectionMessage();
        }
        if (currentLevel != null) {
            userState.setDbLevelId(currentLevel.getId());
        }
        for (DbInventoryItem inventoryItem : inventoryItems) {
            userState.addInventoryItem(inventoryItem.getId());
        }
        for (DbInventoryArtifact inventoryArtifact : inventoryArtifacts) {
            userState.addInventoryArtifact(inventoryArtifact.getId());
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

    @Override
    public String toString() {
        return "DbUserState: user=" + user;
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
