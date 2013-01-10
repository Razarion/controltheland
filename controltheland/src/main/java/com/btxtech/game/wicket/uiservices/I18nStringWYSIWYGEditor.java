package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.db.DbI18nString;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 08.01.13
 * Time: 20:39
 */
public class I18nStringWYSIWYGEditor extends Panel {
    public I18nStringWYSIWYGEditor(String id) {
        super(id);
        WysiwygEditor additionalDescription = new WysiwygEditor("editor");
        additionalDescription.setDefaultModel(new IModel<String>() {
            @Override
            public String getObject() {
                return ((DbI18nString) getDefaultModelObject()).getString();
            }

            @Override
            public void setObject(String string) {
                ((DbI18nString) getDefaultModelObject()).putString(string);
            }

            @Override
            public void detach() {
            }
        });
        add(additionalDescription);
    }
}
