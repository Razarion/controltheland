package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.db.DbI18nString;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString dbI18nName = new DbI18nString();

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

    public DbI18nString getDbI18nName() {
        return dbI18nName;
    }
}
