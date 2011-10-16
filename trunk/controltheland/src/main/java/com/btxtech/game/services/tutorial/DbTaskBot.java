package com.btxtech.game.services.tutorial;

import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 12.10.2011
 * Time: 13:09:38
 */
@Entity(name = "TUTORIAL_TASK_BOT")
public class DbTaskBot implements CrudChild<DbTaskConfig> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBotConfig dbBotConfig;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbTaskConfig", insertable = false, updatable = false, nullable = false)
    private DbTaskConfig dbTaskConfig;

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

    @Override
    public void setParent(DbTaskConfig dbTaskConfig) {
        this.dbTaskConfig = dbTaskConfig;
    }

    public DbBotConfig getDbBotConfig() {
        return dbBotConfig;
    }

    public void setDbBotConfig(DbBotConfig dbBotConfig) {
        this.dbBotConfig = dbBotConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTaskBot)) return false;

        DbTaskBot that = (DbTaskBot) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
