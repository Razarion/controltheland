package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.statistics.StatisticsEntry;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.condition.DbGenericComparisonValue;
import org.hibernate.annotations.Cascade;

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
    @ManyToOne
    private User user;
    @OneToOne
    private DbBase base;
    @ManyToOne
    private DbLevel currentLevel;
    @ManyToOne(optional = false)
    private BackupEntry backupEntry;
    private int xp;
    private boolean sendResurrectionMessage;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_LEVEL_TASK_DONE",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "levelTask")})
    private Collection<DbLevelTask> levelTasksDone;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BACKUP_USER_STATUS_LEVEL_TASK_ACTIVE",
            joinColumns = {@JoinColumn(name = "userState")},
            inverseJoinColumns = {@JoinColumn(name = "levelTask")})
    private Collection<DbLevelTask> levelTasksActive;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbUserState")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbGenericComparisonValue> dbGenericComparisonValues;
    @Embedded
    private StatisticsEntry statisticsEntry;

    /**
     * Used by hibernate
     */
    protected DbUserState() {
    }

    public DbUserState(BackupEntry backupEntry, UserState userState, DbLevel dbLevel) {
        this.backupEntry = backupEntry;
        user = userState.getUser();
        xp = userState.getXp();
        currentLevel = dbLevel;
        sendResurrectionMessage = userState.isSendResurrectionMessage();
    }

    public UserState createUserState() {
        UserState userState = new UserState();
        userState.setUser(user);
        userState.setXp(xp);
        if (sendResurrectionMessage) {
            userState.setSendResurrectionMessage();
        }
        if (currentLevel != null) {
            userState.setDbLevelId(currentLevel.getId());
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

    public Collection<DbLevelTask> getLevelTasksActive() {
        return levelTasksActive;
    }

    public void setLevelTasksActive(Collection<DbLevelTask> levelTasksActive) {
        this.levelTasksActive = levelTasksActive;
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
