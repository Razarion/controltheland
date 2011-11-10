package com.btxtech.game.services.terrain;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.11.2011
 * Time: 01:41:03
 */
@Entity(name = "TERRAIN_IMAGE_GROUP")
public class DbTerrainImageGroup implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbTerrainImage> dbTerrainImages;
    private String htmlBackgroundColor;
    @Transient
    private CrudChildServiceHelper<DbTerrainImage> terrainImageCrud;

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

    public String getHtmlBackgroundColor() {
        return htmlBackgroundColor;
    }

    public void setHtmlBackgroundColor(String htmlBackgroundColor) {
        this.htmlBackgroundColor = htmlBackgroundColor;
    }

    @Override
    public void init(UserService userService) {
        dbTerrainImages = new ArrayList<DbTerrainImage>();
        htmlBackgroundColor = "#FFFFFF";
    }

    public CrudChildServiceHelper<DbTerrainImage> getTerrainImageCrud() {
        if (terrainImageCrud == null) {
            terrainImageCrud = new CrudChildServiceHelper<DbTerrainImage>(dbTerrainImages, DbTerrainImage.class, this);
        }
        return terrainImageCrud;
    }

    @Override
    public void setParent(Object o) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTerrainImageGroup that = (DbTerrainImageGroup) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
