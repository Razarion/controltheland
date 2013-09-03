package com.btxtech.game.services.media;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 15.08.12
 * Time: 13:50
 */
@Entity(name = "SOUND_COMMON")
public class DbCommonSound implements CrudChild {
    public enum Type {
        UNIT_LOST,
        BUILDING_LOST,
        UNIT_KILLED,
        BUILDING_KILLED,
        BACKGROUND_MUSIC
    }

    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Type type;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbSound dbSound;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void init(UserService userService) {
        // Not used
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public DbSound getDbSound() {
        return dbSound;
    }

    public void setDbSound(DbSound dbSound) {
        this.dbSound = dbSound;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbCommonSound that = (DbCommonSound) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
