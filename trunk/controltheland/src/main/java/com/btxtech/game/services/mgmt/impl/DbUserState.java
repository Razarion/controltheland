package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    @OneToOne(cascade = CascadeType.ALL)
    private DbAbstractComparisonBackup dbAbstractComparisonBackup;// TODO
    private int xp;
    private boolean sendResurrectionMessage;
//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(
//            name = "BACKUP_USER_STATUS_LEVEL_TASK_DONE",
//            joinColumns = @JoinColumn(name = "userState")
//    )
//    @Column(name = "levelTaskDone")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable
            (
                    name = "BACKUP_USER_STATUS_LEVEL_TASK_DONE",
                    joinColumns = {@JoinColumn(name = "userState")},
                    inverseJoinColumns = {@JoinColumn(name = "levelTask")}
            )
    private Collection<DbLevelTask> levelTasksDone;

    /**
     * Used by hibernate
     */
    protected DbUserState() {
    }

    public DbUserState(BackupEntry backupEntry, UserState userState, UserGuidanceService userGuidanceService) {
        this.backupEntry = backupEntry;
        user = userState.getUser();
        xp = userState.getXp();
        currentLevel = userGuidanceService.getDbLevel(userState.getDbLevelId());
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

    public void setDbAbstractComparisonBackup(DbAbstractComparisonBackup dbAbstractComparisonBackup) {
        this.dbAbstractComparisonBackup = dbAbstractComparisonBackup;
    }

    public DbAbstractComparisonBackup getDbAbstractComparisonBackup() {
        return dbAbstractComparisonBackup;
    }

    public Collection<DbLevelTask> getLevelTasksDone() {
        return levelTasksDone;
    }

    public void setLevelTasksDone(Collection<DbLevelTask> levelTasksDone) {
        this.levelTasksDone = levelTasksDone;
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
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
