package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.layout.DbContentDynamicHtml;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.WysiwygEditor;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
        add(new EditPanel("edit", dbContentDynamicHtml, contentId, null, false, false));

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
        }.setEscapeModelStrings(dbContentDynamicHtml.getEditorType().isEscapeHtml()));

        WysiwygEditor wysiwygEditor = new WysiwygEditor("htmlTextArea", new LoadableDetachableModel<String>() {
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
        add(wysiwygEditor);

        if (dbContentDynamicHtml.getCssClass() != null) {
            add(new AttributeModifier("class", dbContentDynamicHtml.getCssClass()));
        }
    }
}
