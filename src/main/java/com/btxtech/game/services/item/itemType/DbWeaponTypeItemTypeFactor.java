package com.btxtech.game.services.item.itemType;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 12.01.14
 * Time: 11:54
 */
@Entity(name = "ITEM_WEAPON_TYPE_FACTOR")
public class DbWeaponTypeItemTypeFactor implements CrudChild<DbWeaponType> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbWeaponType weaponType;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType dbBaseItemType;
    private double factor;

    @Override
    public Integer getId() {
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
        factor = 1.0;
    }

    @Override
    public void setParent(DbWeaponType weaponType) {
        this.weaponType = weaponType;
    }

    @Override
    public DbWeaponType getParent() {
        return weaponType;
    }

    public DbBaseItemType getDbBaseItemType() {
        return dbBaseItemType;
    }

    public void setDbBaseItemType(DbBaseItemType dbBaseItemType) {
        this.dbBaseItemType = dbBaseItemType;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbWeaponTypeItemTypeFactor that = (DbWeaponTypeItemTypeFactor) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
