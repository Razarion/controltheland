package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.common.db.DbI18nString;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("STATIC_HTML")
public class DbContentStaticHtml extends DbContent {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString dbI18nHtml = new DbI18nString();
    private DbExpressionProperty.EditorType editorType = DbExpressionProperty.EditorType.PLAIN_TEXT_FILED;

    public DbI18nString getDbI18nHtml() {
        return dbI18nHtml;
    }

    public DbExpressionProperty.EditorType getEditorType() {
        return editorType;
    }

    public void setEditorType(DbExpressionProperty.EditorType editorType) {
        this.editorType = editorType;
    }
}
