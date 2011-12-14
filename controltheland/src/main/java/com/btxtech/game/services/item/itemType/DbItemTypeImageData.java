package com.btxtech.game.services.item.itemType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 13.12.2011
 * Time: 18:03:14
 */
@Entity(name = "ITEM_TYPE_IMAGE_DATA")
public class DbItemTypeImageData {
    @Id
    @GeneratedValue
    private Integer id;
    private String contentType;
    @Column(length = 500000)
    private byte[] data;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemTypeImageData that = (DbItemTypeImageData) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

}
