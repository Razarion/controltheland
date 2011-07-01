package com.btxtech.game.services.cms;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 30.06.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("CONTENT_PAGE_LINK")
public class DbContentPageLink extends DbContent {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbPage dbPage;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage dbCmsImage;

    public DbPage getDbPage() {
        return dbPage;
    }

    public void setDbPage(DbPage dbPage) {
        this.dbPage = dbPage;
    }

    public DbCmsImage getDbCmsImage() {
        return dbCmsImage;
    }

    public void setDbCmsImage(DbCmsImage dbCmsImage) {
        this.dbCmsImage = dbCmsImage;
    }

    @Override
    public void init() {
        setupDefaultRights();
    }
}
