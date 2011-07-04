package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.DbContentDynamicHtml;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 04.07.2011
 * Time: 13:26:28
 */
public class ContentDynamicHtml extends Panel {
    @SpringBean
    private ContentService contentService;
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentDynamicHtml(String id, DbContentDynamicHtml dbContentDynamicHtml) {
        super(id);
        contentId = dbContentDynamicHtml.getId();
        add(new EditPanel("edit", contentId, null, false, false));

        add(new Label("html", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {
                return contentService.getDynamicHtml(contentId);
            }
        }) {
            @Override
            public boolean isVisible() {
                return cmsUiService.getEditMode(contentId) == null;
            }
        }.setEscapeModelStrings(dbContentDynamicHtml.getEscapeMarkup()));

        TextArea<String> contentArea = new TextArea<String>("htmlTextArea", new LoadableDetachableModel<String>() {
            @Override
            public void setObject(String s) {
                super.setObject(s);
                contentService.setDynamicHtml(contentId, s);
            }

            @Override
            protected String load() {
                return contentService.getDynamicHtml(contentId);
            }
        }) {
            @Override
            public boolean isVisible() {
                return cmsUiService.getEditMode(contentId) != null;
            }
        };
        TinyMCESettings tinyMCESettings = new TinyMCESettings(TinyMCESettings.Theme.advanced);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.link, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.unlink, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        add(contentArea);
    }
}
