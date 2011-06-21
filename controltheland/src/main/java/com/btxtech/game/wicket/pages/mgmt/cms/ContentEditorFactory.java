package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbBeanTable;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentBook;
import com.btxtech.game.services.cms.DbContentContainer;
import com.btxtech.game.services.cms.DbContentDetailLink;
import com.btxtech.game.services.cms.DbExpressionProperty;
import com.btxtech.game.services.cms.DbStaticProperty;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;

/**
 * User: beat
 * Date: 20.06.2011
 * Time: 17:34:25
 */
public class ContentEditorFactory {
    public static MgmtWebPage createContentEditor(DbContent dbContent) {
        if (dbContent instanceof DbBeanTable) {
            return new BeanTableEditor((DbBeanTable) dbContent);
        } else if (dbContent instanceof DbContentBook) {
            return new ContentBookEditor((DbContentBook) dbContent);
        } else if (dbContent instanceof DbExpressionProperty) {
            return new ExpressionPropertyEditor((DbExpressionProperty) dbContent);
        } else if (dbContent instanceof DbContentContainer) {
            return new ContentContainerEditor((DbContentContainer) dbContent);
        } else if (dbContent instanceof DbContentDetailLink) {
            return new ContentDetailLinkEditor();
        } else if (dbContent instanceof DbStaticProperty) {
            return new StaticPropertyEditor((DbStaticProperty) dbContent);
        } else {
            throw new IllegalArgumentException("Unknown DbContent: " + dbContent);
        }
    }

    public enum DbContentEnum {
        BEAN_TABLE(DbBeanTable.class, "Bean table"),
        CONTENT_CONTAINER(DbContentContainer.class, "Content Container"),
        CONTENT_BOOK(DbContentBook.class, "Content Book"),
        CONTENT_LINK(DbContentDetailLink.class, "Content Link"),
        STATIC_PROPERTY(DbStaticProperty.class, "Static Property"),
        EXPRESSION_PROPERTY(DbExpressionProperty.class, "Expression Property");

        private Class<? extends DbContent> createClass;
        private String displayName;

        DbContentEnum(Class<? extends DbContent> createClass, String displayName) {
            this.createClass = createClass;
            this.displayName = displayName;
        }

        public Class<? extends DbContent> getCreateClass() {
            return createClass;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
