package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.RectangleUserType;
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 11.09.12
 * Time: 23:48
 */
@Entity(name = "REGION")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbRegion implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "REGION_RECTANGLES")
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "x"), @Column(name = "y"), @Column(name = "width"), @Column(name = "height")})
    private Collection<Rectangle> rectangles;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void init(UserService userService) {
        rectangles = new ArrayList<>();
    }

    public Region createRegion() {
        return new Region(id, new ArrayList<>(rectangles));
    }

    public void setRegion(Region region) {
        this.rectangles.clear();
        this.rectangles.addAll(region.getRectangles());
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

    @Override
    public String toString() {
        return "DbRegion{id=" + id + '}';
    }
}
