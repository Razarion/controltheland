package com.btxtech.game.services.planet.db;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 22:03
 */
@Entity(name = "BOX_REGION")
public class DbBoxRegion implements CrudChild<DbPlanet>, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "dbBoxRegion", nullable = false)
    private Collection<DbBoxRegionCount> dbBoxRegionCounts;
    private long minInterval;
    private long maxInterval;
    @OneToOne(fetch = FetchType.LAZY)
    private DbRegion region;
    private int itemFreeRange;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbPlanet dbPlanet;

    @Transient
    private CrudChildServiceHelper<DbBoxRegionCount> boxRegionCountCrud;

    @Override
    public Serializable getId() {
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
        dbBoxRegionCounts = new ArrayList<>();
    }

    @Override
    public void setParent(DbPlanet dbPlanet) {
        this.dbPlanet = dbPlanet;
    }

    @Override
    public DbPlanet getParent() {
        return dbPlanet;
    }

    public long getMinInterval() {
        return minInterval;
    }

    public void setMinInterval(long minInterval) {
        this.minInterval = minInterval;
    }

    public long getMaxInterval() {
        return maxInterval;
    }

    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval;
    }

    public CrudChildServiceHelper<DbBoxRegionCount> getBoxRegionCountCrud() {
        if (boxRegionCountCrud == null) {
            boxRegionCountCrud = new CrudChildServiceHelper<>(dbBoxRegionCounts, DbBoxRegionCount.class, this);
        }
        return boxRegionCountCrud;
    }

    public DbRegion getRegion() {
        return region;
    }

    public void setRegion(DbRegion region) {
        this.region = region;
    }

    public int getItemFreeRange() {
        return itemFreeRange;
    }

    public void setItemFreeRange(int itemFreeRange) {
        this.itemFreeRange = itemFreeRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBoxRegion)) return false;

        DbBoxRegion that = (DbBoxRegion) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
