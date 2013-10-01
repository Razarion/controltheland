package com.btxtech.game.services.media;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: beat
 * Date: 13.08.12
 * Time: 22:59
 */
@Entity(name = "SOUND_LIBRARY")
public class DbSound implements CrudChild, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Column(length = 50000000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] dataMp3;
    @Column(length = 50000000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] dataOgg;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(Object o) {
        // No parent
    }

    @Override
    public Object getParent() {
        return null;
    }

    public byte[] getDataMp3() {
        return dataMp3;
    }

    public void setDataMp3(byte[] dataMp3) {
        this.dataMp3 = dataMp3;
    }

    public byte[] getDataOgg() {
        return dataOgg;
    }

    public void setDataOgg(byte[] dataOgg) {
        this.dataOgg = dataOgg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSound dbSound = (DbSound) o;

        return id != null && id.equals(dbSound.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbSound{id=" + id + ", name='" + name + "'}";
    }
}
