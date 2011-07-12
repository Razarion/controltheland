package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbExpressionProperty;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 23.06.2011
 * Time: 10:53:23
 */
public class WritePanel extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private CmsService cmsService;

    public WritePanel(String id, Object value, final BeanIdPathElement beanIdPathElement, DbExpressionProperty dbExpressionProperty) {
        super(id);
        final int contentId = dbExpressionProperty.getId();
        add(new TextField("field", new LoadableDetachableModel(value) {

            @Override
            public void setObject(Object object) {
                super.setObject(object);
                cmsUiService.setDataProviderBean(object, beanIdPathElement, contentId);
            }

            @Override
            protected Object load() {
                return cmsUiService.getDataProviderBean(beanIdPathElement);
            }
        }) {
            @Override
            public boolean isVisible() {
                return ((DbExpressionProperty) cmsService.getDbContent(contentId)).getEscapeMarkup();
            }
        });
        TextArea contentArea = new TextArea("textArea", new LoadableDetachableModel() {
            @Override
            public void setObject(Object s) {
                super.setObject(s);
                cmsUiService.setDataProviderBean(s, beanIdPathElement, contentId);
            }

            @Override
            protected Object load() {
                return cmsUiService.getDataProviderBean(beanIdPathElement);
            }
        }) {
            @Override
            public boolean isVisible() {
                return !((DbExpressionProperty) cmsService.getDbContent(contentId)).getEscapeMarkup();
            }
        };
        TinyMCESettings tinyMCESettings = new TinyMCESettings(TinyMCESettings.Theme.advanced);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.link, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.unlink, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        add(contentArea);
    }
}
