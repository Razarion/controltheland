package com.btxtech.game.services.tutorial;

import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 18.03.2011
 * Time: 13:09:38
 */
@Entity(name = "TUTORIAL_TASK_BOT_TO_STOP")
public class DbTaskBotToStop implements CrudChild<DbAbstractTaskConfig> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBotConfig dbBotConfig;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbAbstractTaskConfig dbTaskConfig;

    @Override
    public Serializable getId() {
        return id;
    }

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
    }

    public DbBotConfig getDbBotConfig() {
        return dbBotConfig;
    }

    public void setDbBotConfig(DbBotConfig dbBotConfig) {
        this.dbBotConfig = dbBotConfig;
    }

    @Override
    public void setParent(DbAbstractTaskConfig dbTaskConfig) {
        this.dbTaskConfig = dbTaskConfig;
    }

    @Override
    public DbAbstractTaskConfig getParent() {
        return dbTaskConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbTaskBotToStop)) {
            return false;
        }

        DbTaskBotToStop that = (DbTaskBotToStop) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
