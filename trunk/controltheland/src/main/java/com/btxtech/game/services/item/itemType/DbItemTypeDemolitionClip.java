package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.media.DbClip;
import com.btxtech.game.services.media.DbClipUtil;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 02.11.12
 * Time: 10:57
 */
@Entity(name = "ITEM_TYPE_DEMOLITION_CLIP")
@TypeDefs({@TypeDef(name = "index", typeClass = IndexUserType.class)})
public class DbItemTypeDemolitionClip implements CrudChild<DbItemTypeDemolitionStep> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbItemTypeDemolitionStep dbItemTypeDemolitionStep;
    @ManyToOne
    private DbClip dbClip;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ITEM_TYPE_DEMOLITION_CLIP_POSITION",
            joinColumns = @JoinColumn(name = "itemTypeDemolitionClipId"))
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    @OrderColumn(name = "imageIndex")
    private List<Index> positions;

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
        positions = new ArrayList<>();
    }

    @Override
    public void setParent(DbItemTypeDemolitionStep dbItemTypeDemolitionStep) {
        this.dbItemTypeDemolitionStep = dbItemTypeDemolitionStep;
    }

    @Override
    public DbItemTypeDemolitionStep getParent() {
        return dbItemTypeDemolitionStep;
    }

    public DbClip getDbClip() {
        return dbClip;
    }

    public void setDbClip(DbClip dbClip) {
        this.dbClip = dbClip;
    }

    public ItemClipPosition createItemClipPosition() {
        return DbClipUtil.createItemClipPosition(dbClip, positions);
    }

    public void setPositions(Index[] positions) {
        this.positions.clear();
        Collections.addAll(this.positions, positions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemTypeDemolitionClip that = (DbItemTypeDemolitionClip) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
