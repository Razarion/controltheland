package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.tutorial.GameFlow;
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
 * Date: 19.01.2012
 * Time: 15:15:59
 */
@Entity(name = "GUIDANCE_GAME_FLOW")
public class DbGameFlow implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private GameFlow.Type type;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbLevelTask dbLevelTask;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbLevel dbLevel;

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
    public void setParent(Object o) {
    }

    public DbLevelTask getDbLevelTask() {
        return dbLevelTask;
    }

    public void setDbLevelTask(DbLevelTask dbLevelTask) {
        this.dbLevelTask = dbLevelTask;
    }

    public GameFlow.Type getType() {
        return type;
    }

    public void setType(GameFlow.Type type) {
        this.type = type;
    }

    public DbLevel getDbLevel() {
        return dbLevel;
    }

    public void setDbLevel(DbLevel dbLevel) {
        this.dbLevel = dbLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbGameFlow that = (DbGameFlow) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
