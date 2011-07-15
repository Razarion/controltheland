package com.btxtech.game.services.utg;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 15.04.2011
 * Time: 17:43:09
 */
@Entity(name = "GUIDANCE_RESURRECTION")
public class DbResurrection implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    // ----- New Base -----
    @ManyToOne
    private DbBaseItemType startItemType;
    @ManyToOne
    private DbTerritory dbTerritory;
    private int startItemFreeRange;
    private int money;

    public Integer getId() {
        return id;
    }

    public DbBaseItemType getStartItemType() {
        return startItemType;
    }

    public void setStartItemType(DbBaseItemType startItemType) {
        this.startItemType = startItemType;
    }

    public DbTerritory getDbTerritory() {
        return dbTerritory;
    }

    public void setDbTerritory(DbTerritory dbTerritory) {
        this.dbTerritory = dbTerritory;
    }

    public int getStartItemFreeRange() {
        return startItemFreeRange;
    }

    public void setStartItemFreeRange(int startItemFreeRange) {
        this.startItemFreeRange = startItemFreeRange;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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
        // Ignore
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbResurrection)) return false;

        DbResurrection that = (DbResurrection) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
