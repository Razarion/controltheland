package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbBoxRegion implements CrudChild, CrudParent {
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
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "regionX"), @Column(name = "regionY"), @Column(name = "regionWidth"), @Column(name = "regionHeight")})
    private Rectangle region;
    private int itemFreeRange;

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
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        throw new UnsupportedOperationException();
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

    public Rectangle getRegion() {
        return region;
    }

    public void setRegion(Rectangle region) {
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
