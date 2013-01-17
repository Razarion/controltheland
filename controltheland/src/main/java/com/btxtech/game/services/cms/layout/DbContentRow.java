package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.common.db.DbI18nString;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 11:55:54
 */
@Entity
@DiscriminatorValue("CONTENT_ROW")
public class DbContentRow extends DbContent {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "parent")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DbContent dbContent;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString dbI18nName = new DbI18nString();

    public DbContent getDbContent() {
        return dbContent;
    }

    public void setDbContent(DbContent dbContent) {
        this.dbContent = dbContent;
    }

    public DbI18nString getDbI18nName() {
        return dbI18nName;
    }

    @Override
    public Collection<DbContent> getChildren() {
        if (dbContent != null) {
            return Collections.singletonList(dbContent);
        } else {
            return Collections.emptyList();

        }
    }
}
