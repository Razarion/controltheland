package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 02.11.12
 * Time: 10:57
 */
@Entity(name = "ITEM_TYPE_DEMOLITION_CLIPS")
public class DbItemTypeDemolitionClips implements CrudParent, CrudChild<DbItemType> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbItemType itemType;
    private int demolitionStep;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dbItemTypeDemolitionClips", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbItemTypeDemolitionClip> dbItemTypeDemolitionClips;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void init(UserService userService) {
        dbItemTypeDemolitionClips = new ArrayList<>();
    }

    @Override
    public void setParent(DbItemType dbItemType) {
        itemType = dbItemType;
    }

    @Override
    public DbItemType getParent() {
        return itemType;
    }

    public int getDemolitionStep() {
        return demolitionStep;
    }

    public void setDemolitionStep(int demolitionStep) {
        this.demolitionStep = demolitionStep;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public void setClips(Collection<ItemClipPosition> itemClipPositions, ClipService clipService) {
        CrudChildServiceHelper<DbItemTypeDemolitionClip> crud = new CrudChildServiceHelper<DbItemTypeDemolitionClip>(dbItemTypeDemolitionClips, DbItemTypeDemolitionClip.class, this);
        crud.deleteAllChildren();
        for (ItemClipPosition itemClipPosition : itemClipPositions) {
            DbItemTypeDemolitionClip dbItemTypeDemolitionClip = crud.createDbChild();
            dbItemTypeDemolitionClip.setDbClip(clipService.getClipLibraryCrud().readDbChild(itemClipPosition.getClipId()));
            dbItemTypeDemolitionClip.setPositions(itemClipPosition.getPositions());
        }
    }

    public Collection<ItemClipPosition> createItemClips() {
        if (dbItemTypeDemolitionClips == null || dbItemTypeDemolitionClips.isEmpty()) {
            return null;
        }
        Collection<ItemClipPosition> itemClipPositions = new ArrayList<>();
        for (DbItemTypeDemolitionClip dbItemTypeDemolitionClip : dbItemTypeDemolitionClips) {
            ItemClipPosition itemClipPosition = dbItemTypeDemolitionClip.createItemClipPosition();
            if (itemClipPosition != null) {
                itemClipPositions.add(itemClipPosition);
            }
        }
        return itemClipPositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemTypeDemolitionClips that = (DbItemTypeDemolitionClips) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
