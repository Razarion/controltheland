package com.btxtech.game.services.utg;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.territory.DbTerritory;
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
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 17.01.2012
 * Time: 14:44:56
 */
@Entity(name = "GUIDANCE_QUEST_HUB")
public class DbQuestHub implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    @OrderBy
    private int orderIndex;    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dbQuestHub", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", base = 0)
    private List<DbLevel> dbLevels;
    private String name;
    // ----- New Base -----
    private boolean realBaseRequired;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType startItemType;
    private int startItemFreeRange;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbTerritory startTerritory;
    private int startMoney;

    @Transient
    private CrudListChildServiceHelper<DbLevel> levelCrud;

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
        dbLevels = new ArrayList<DbLevel>();
        realBaseRequired = true;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public boolean isRealBaseRequired() {
        return realBaseRequired;
    }

    public void setRealBaseRequired(boolean realBaseRequiered) {
        this.realBaseRequired = realBaseRequiered;
    }

    public DbBaseItemType getStartItemType() {
        return startItemType;
    }

    public void setStartItemType(DbBaseItemType startItemType) {
        this.startItemType = startItemType;
    }

    public int getStartItemFreeRange() {
        return startItemFreeRange;
    }

    public void setStartItemFreeRange(int startItemFreeRange) {
        this.startItemFreeRange = startItemFreeRange;
    }

    public DbTerritory getStartTerritory() {
        return startTerritory;
    }

    public void setStartTerritory(DbTerritory startTerritory) {
        this.startTerritory = startTerritory;
    }

    public int getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(int startMoney) {
        this.startMoney = startMoney;
    }

    public CrudListChildServiceHelper<DbLevel> getLevelCrud() {
        if (levelCrud == null) {
            levelCrud = new CrudListChildServiceHelper<DbLevel>(dbLevels, DbLevel.class, this);
        }
        return levelCrud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbQuestHub)) return false;
        
        DbQuestHub that = (DbQuestHub) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + name + " id: " + id;
    }
}
