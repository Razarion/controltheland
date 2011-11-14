package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
    private DbAbstractLevel currentAbstractLevel;
    @OneToOne(cascade = CascadeType.ALL)
    private DbUserItemTypeAccess userItemTypeAccess;
    @ManyToOne(optional = false)
    private BackupEntry backupEntry;
    @OneToOne(cascade = CascadeType.ALL)
    private DbAbstractComparisonBackup dbAbstractComparisonBackup;

    /**
     * Used by hibernate
     */
    protected DbUserState() {
    }

    public DbUserState(BackupEntry backupEntry, UserState userState) {
        this.backupEntry = backupEntry;
        user = userState.getUser();
        currentAbstractLevel = userState.getCurrentAbstractLevel();
        if (userState.getUserItemTypeAccess() != null) {
            userItemTypeAccess = new DbUserItemTypeAccess(userState.getUserItemTypeAccess());
        }
    }

    public UserState createUserState(UserGuidanceService userGuidanceService) {
        UserState userState = new UserState();
        userState.setUser(user);
        if (currentAbstractLevel != null) {
            userState.setCurrentAbstractLevel(userGuidanceService.getDbLevel(currentAbstractLevel.getId()));
        }
        if (userItemTypeAccess != null) {
            userState.setUserItemTypeAccess(userItemTypeAccess.createUserItemTypeAccess());
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
