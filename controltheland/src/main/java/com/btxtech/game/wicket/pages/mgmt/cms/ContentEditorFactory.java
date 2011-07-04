package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentBook;
import com.btxtech.game.services.cms.DbContentContainer;
import com.btxtech.game.services.cms.DbContentDetailLink;
import com.btxtech.game.services.cms.DbContentDynamicHtml;
import com.btxtech.game.services.cms.DbContentLink;
import com.btxtech.game.services.cms.DbContentList;
import com.btxtech.game.services.cms.DbContentPageLink;
import com.btxtech.game.services.cms.DbContentPlugin;
import com.btxtech.game.services.cms.DbExpressionProperty;
import com.btxtech.game.services.cms.DbContentStaticHtml;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;

/**
 * User: beat
 * Date: 20.06.2011
 * Time: 17:34:25
 */
public class ContentEditorFactory {
    public static MgmtWebPage createContentEditor(DbContent dbContent) {
        for (DbContentEnum dbContentEnum : DbContentEnum.values()) {
            if (dbContentEnum.getCreateClass().equals(dbContent.getClass())) {
                return dbContentEnum.createContentEditor(dbContent);
            }
        }
        throw new IllegalArgumentException("Unknown DbContent: " + dbContent);
    }

    public enum DbContentEnum {
        CONTENT_LIST(DbContentList.class, "Content List") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentListEditor((DbContentList) dbContent);
            }},
        CONTENT_CONTAINER(DbContentContainer.class, "Content Container") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentContainerEditor((DbContentContainer) dbContent);
            }},
        CONTENT_BOOK(DbContentBook.class, "Content Book") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentBookEditor((DbContentBook) dbContent);
            }},
        CONTENT_LINK(DbContentDetailLink.class, "Content Link") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentDetailLinkEditor((DbContentDetailLink) dbContent);
            }},
        STATIC_HTML(DbContentStaticHtml.class, "Static HTML") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentStaticHtmlEditor((DbContentStaticHtml) dbContent);
            }},
        EXPRESSION_PROPERTY(DbExpressionProperty.class, "Expression Property") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ExpressionPropertyEditor((DbExpressionProperty) dbContent);
            }},
        PAGE_LINK(DbContentPageLink.class, "Page Link") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentPageLinkEditor((DbContentPageLink) dbContent);
            }},
        LINK(DbContentLink.class, "Link") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentLinkEditor((DbContentLink) dbContent);
            }},
        PLUGIN(DbContentPlugin.class, "Plugin") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentPluginEditor((DbContentPlugin) dbContent);
            }},
        DYNAMIC_HTML(DbContentDynamicHtml.class, "Dynamic HTML") {

            @Override
            MgmtWebPage createContentEditor(DbContent dbContent) {
                return new ContentDynamicHtmlEditor((DbContentDynamicHtml) dbContent);
            }};

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

        abstract MgmtWebPage createContentEditor(DbContent dbContent);
    }
}
