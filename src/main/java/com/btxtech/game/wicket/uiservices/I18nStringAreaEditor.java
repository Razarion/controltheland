package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.db.DbI18nString;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 08.01.13
 * Time: 20:39
 */
public class I18nStringAreaEditor extends Panel {
    public I18nStringAreaEditor(String id) {
        super(id);
        add(new TextArea<>("text", new IModel<String>() {

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
                // Ignore
            }
        }));
        add(new Label("id", new AbstractReadOnlyModel<Integer>() {

            @Override
            public Integer getObject() {
                return ((DbI18nString) getDefaultModelObject()).getId();
            }
        }));
    }
}
