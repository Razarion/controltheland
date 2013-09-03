package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.jsre.common.packets.StorablePacket;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 29.06.13
 * Time: 14:32
 */
@Entity(name = "BACKUP_USER_STATUS_STORED_MESSAGES")
public class DbStorablePacket {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbUserState userState;
    @Enumerated(EnumType.STRING)
    private StorablePacket.Type type;

    /**
     * Used by Hibernate
     */
    protected DbStorablePacket() {
    }

    public DbStorablePacket(DbUserState userState, StorablePacket storablePacket) {
        this.userState = userState;
        type = storablePacket.getType();
    }

    public Integer getId() {
        return id;
    }

    public StorablePacket.Type getType() {
        return type;
    }

    public void setType(StorablePacket.Type type) {
        this.type = type;
    }

    public StorablePacket createStorablePacket() {
        StorablePacket storablePacket = new StorablePacket();
        storablePacket.setType(type);
        return storablePacket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbStorablePacket)) {
            return false;
        }

        DbStorablePacket userState = (DbStorablePacket) o;
        return id != null && id.equals(userState.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
