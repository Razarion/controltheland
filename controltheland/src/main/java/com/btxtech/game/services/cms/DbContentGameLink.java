package com.btxtech.game.services.cms;

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

    public DbCmsImage getDbCmsImage() {
        return dbCmsImage;
    }

    public void setDbCmsImage(DbCmsImage dbCmsImage) {
        this.dbCmsImage = dbCmsImage;
    }
}
