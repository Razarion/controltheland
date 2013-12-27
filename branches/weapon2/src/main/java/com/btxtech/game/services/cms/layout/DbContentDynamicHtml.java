package com.btxtech.game.services.cms.layout;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 04.07.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("DYNAMIC_HTML")
public class DbContentDynamicHtml extends DbContent {
    private DbExpressionProperty.EditorType editorType = DbExpressionProperty.EditorType.PLAIN_TEXT_FILED;

    public DbExpressionProperty.EditorType getEditorType() {
        return editorType;
    }

    public void setEditorType(DbExpressionProperty.EditorType editorType) {
        this.editorType = editorType;
    }

    @Override
    public String getSpringBeanName() {
        // This is only used for generating the EditMode class
        return "DbContentDynamicHtml " + getId();
    }
}
