package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.cms.DbCmsImage;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 01.07.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("CONTENT_GAME_LINK")
public class DbContentGameLink extends DbContent {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage dbCmsImage;
    private String linkText;

    public DbCmsImage getDbCmsImage() {
        return dbCmsImage;
    }

    public void setDbCmsImage(DbCmsImage dbCmsImage) {
        this.dbCmsImage = dbCmsImage;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }
}
