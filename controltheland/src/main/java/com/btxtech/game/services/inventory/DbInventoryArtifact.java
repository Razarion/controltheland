package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 11:33
 */
@Entity(name = "INVENTORY_ARTIFACT")
public class DbInventoryArtifact implements CrudChild {
    public enum Rareness {
        COMMON("#d6f6ff"),
        UN_COMMON("#70d460"),
        RARE("#1273d2"),
        EPIC("#a042cc"),
        LEGENDARY("#f07d4e");
        private String htmlColor;

        Rareness(String htmlColor) {
            this.htmlColor = htmlColor;
        }

        public String getHtmlColor() {
            return htmlColor;
        }
    }

    @Id
    @GeneratedValue
    private Integer id;
    private Rareness rareness;
    private String name;
    private String imageContentType;
    @Column(length = 500000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;

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
        rareness = Rareness.COMMON;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        throw new UnsupportedOperationException();
    }

    public Rareness getRareness() {
        return rareness;
    }

    public void setRareness(Rareness rareness) {
        this.rareness = rareness;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public InventoryArtifactInfo generateInventoryArtifactInfo() {
        return new InventoryArtifactInfo(name, id, rareness.getHtmlColor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbInventoryArtifact)) return false;

        DbInventoryArtifact that = (DbInventoryArtifact) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}