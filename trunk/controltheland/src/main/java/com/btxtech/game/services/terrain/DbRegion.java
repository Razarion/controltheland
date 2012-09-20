package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.HashSet;

/**
 * User: beat
 * Date: 11.09.12
 * Time: 23:48
 */
@Entity(name = "REGION")
@TypeDef(name = "index", typeClass = IndexUserType.class)
public class DbRegion implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "REGION_TILES")
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private Collection<Index> tiles;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void init(UserService userService) {
        tiles = new HashSet<>();
    }

    public Region createRegion() {
        return new Region(id, new HashSet<>(tiles));
    }

    public void setRegion(Region region) {
        this.tiles.clear();
        this.tiles.addAll(region.getTiles());
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbRegion)) return false;

        DbRegion that = (DbRegion) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
