package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 22:50
 */
@Entity
@DiscriminatorValue("BOX")
public class DbBoxItemType extends DbItemType {
    private long ttl;

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public ItemType createItemType() {
        BoxItemType boxItemType = new BoxItemType();
        setupItemType(boxItemType);
        boxItemType.setTtl(ttl);
        return boxItemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBoxItemType)) return false;

        DbBoxItemType that = (DbBoxItemType) o;

        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId() : System.identityHashCode(this);
    }
}
