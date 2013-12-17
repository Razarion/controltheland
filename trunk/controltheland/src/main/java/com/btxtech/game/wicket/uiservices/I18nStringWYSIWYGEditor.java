package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.db.DbI18nString;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
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
                if (getDefaultModelObject() != null) {
                    return ((DbI18nString) getDefaultModelObject()).getString();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(String string) {
                if (getDefaultModelObject() != null) {
                    ((DbI18nString) getDefaultModelObject()).putString(string);
                }
            }

            @Override
            public void detach() {
            }
        });
        add(additionalDescription);
        add(new Label("id", new AbstractReadOnlyModel<Integer>() {

            @Override
            public Integer getObject() {
                if (getDefaultModelObject() != null) {
                    return ((DbI18nString) getDefaultModelObject()).getId();
                } else {
                    return null;
                }
            }
        }));
    }
}
