package com.btxtech.game.services.cms.layout;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("STATIC_HTML")
public class DbContentStaticHtml extends DbContent {
    @Column(length = 500000)
    private String html;
    private DbExpressionProperty.EditorType editorType = DbExpressionProperty.EditorType.PLAIN_TEXT_FILED;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public DbExpressionProperty.EditorType getEditorType() {
        return editorType;
    }

    public void setEditorType(DbExpressionProperty.EditorType editorType) {
        this.editorType = editorType;
    }
}
