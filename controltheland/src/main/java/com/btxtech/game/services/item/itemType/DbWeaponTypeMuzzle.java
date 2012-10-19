package com.btxtech.game.services.item.itemType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 11.12.2011
 * Time: 19:56:39
 */
@Entity(name = "ITEM_WEAPON_TYPE_MUZZLE")
public class DbWeaponTypeMuzzle {
    @Id
    @GeneratedValue
    private Integer id;
    private int muzzleNumber;
    @ManyToOne
    private DbWeaponType weaponType;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "weaponTypeMuzzle", orphanRemoval = true)
    private Collection<DbWeaponTypeMuzzlePosition> positions;

    public int getMuzzleNumber() {
        return muzzleNumber;
    }

    public void setMuzzleNumber(int muzzleNumber) {
        this.muzzleNumber = muzzleNumber;
    }

    public DbWeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(DbWeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public Collection<DbWeaponTypeMuzzlePosition> getPositions() {
        return positions;
    }

    public void setPositions(Collection<DbWeaponTypeMuzzlePosition> positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbWeaponTypeMuzzle that = (DbWeaponTypeMuzzle) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
