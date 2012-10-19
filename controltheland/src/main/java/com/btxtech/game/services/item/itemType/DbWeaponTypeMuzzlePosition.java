package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.common.db.IndexUserType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 11.12.2011
 * Time: 19:56:50
 */
@Entity(name = "ITEM_WEAPON_TYPE_MUZZLE_POSITION")
@TypeDefs({@TypeDef(name = "index", typeClass = IndexUserType.class)})
public class DbWeaponTypeMuzzlePosition {
    @Id
    @GeneratedValue
    private Integer id;
    private int imageNumber;
    @ManyToOne
    private DbWeaponTypeMuzzle weaponTypeMuzzle;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private Index position;

    public int getImageNumber() {
        return imageNumber;
    }

    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }

    public DbWeaponTypeMuzzle getWeaponTypeMuzzle() {
        return weaponTypeMuzzle;
    }

    public void setWeaponTypeMuzzle(DbWeaponTypeMuzzle weaponTypeMuzzle) {
        this.weaponTypeMuzzle = weaponTypeMuzzle;
    }

    public Index getPosition() {
        return position;
    }

    public void setPosition(Index position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbWeaponTypeMuzzlePosition that = (DbWeaponTypeMuzzlePosition) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

}
