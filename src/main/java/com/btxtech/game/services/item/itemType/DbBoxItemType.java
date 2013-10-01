package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.planet.db.DbBoxRegionCount;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 22:50
 */
@Entity
@DiscriminatorValue("BOX")
public class DbBoxItemType extends DbItemType {
    private long ttl;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "dbBoxItemType", nullable = false)
    private Collection<DbBoxItemTypePossibility> dbBoxItemTypePossibilities;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dbBoxItemType")
    private Collection<DbBoxRegionCount> dbBoxRegionCounts;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dbBoxItemType")
    private Collection<DbBaseItemType> dbBaseItemTypes;


    @Transient
    private CrudChildServiceHelper<DbBoxItemTypePossibility> boxPossibilityCrud;

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

    public CrudChildServiceHelper<DbBoxItemTypePossibility> getBoxPossibilityCrud() {
        if (boxPossibilityCrud == null) {
            boxPossibilityCrud = new CrudChildServiceHelper<>(dbBoxItemTypePossibilities, DbBoxItemTypePossibility.class, this);
        }
        return boxPossibilityCrud;
    }

    @Override
    public void init(UserService userService) {
        super.init(userService);
        dbBoxItemTypePossibilities = new ArrayList<>();
    }

    public Collection<DbBoxRegionCount> getDbBoxRegionCounts() {
        return dbBoxRegionCounts;
    }

    public Collection<DbBaseItemType> getDbBaseItemTypes() {
        return dbBaseItemTypes;
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
