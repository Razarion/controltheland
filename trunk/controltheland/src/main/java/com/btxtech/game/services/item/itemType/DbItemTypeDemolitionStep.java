package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.DemolitionStepSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 02.11.12
 * Time: 10:57
 */
@Entity(name = "ITEM_TYPE_DEMOLITION_STEP")
public class DbItemTypeDemolitionStep implements CrudParent, CrudChild<DbItemType> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbItemType itemType;
    private int step;
    private int animationFrames;
    private int animationDuration;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dbItemTypeDemolitionStep", orphanRemoval = true)
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

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public int getStep() {
        return step;
    }

    public int getAnimationFrames() {
        return animationFrames;
    }

    public void setDemolitionStepSpriteMap(DemolitionStepSpriteMap demolitionStep, int step, ClipService clipService) {
        this.step = step;
        animationFrames = demolitionStep.getAnimationFrames();
        animationDuration = demolitionStep.getAnimationDuration();
        setClips(demolitionStep.getItemClipPositions(),clipService);
    }

    private void setClips(Collection<ItemClipPosition> itemClipPositions, ClipService clipService) {
        CrudChildServiceHelper<DbItemTypeDemolitionClip> crud = new CrudChildServiceHelper<DbItemTypeDemolitionClip>(dbItemTypeDemolitionClips, DbItemTypeDemolitionClip.class, this);
        crud.deleteAllChildren();
        if(itemClipPositions == null || itemClipPositions.isEmpty()) {
            return;
        }
        for (ItemClipPosition itemClipPosition : itemClipPositions) {
            DbItemTypeDemolitionClip dbItemTypeDemolitionClip = crud.createDbChild();
            dbItemTypeDemolitionClip.setDbClip(clipService.getClipLibraryCrud().readDbChild(itemClipPosition.getClipId()));
            dbItemTypeDemolitionClip.setPositions(itemClipPosition.getPositions());
        }
    }

    public DemolitionStepSpriteMap createDemolitionStepSpriteMap() {
        return new DemolitionStepSpriteMap(animationFrames, animationDuration, createItemClips());
    }

    private Collection<ItemClipPosition> createItemClips() {
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

        DbItemTypeDemolitionStep that = (DbItemTypeDemolitionStep) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
