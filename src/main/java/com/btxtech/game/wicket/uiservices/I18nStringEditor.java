package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.db.DbI18nString;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 08.01.13
 * Time: 20:39
 */
public class I18nStringEditor extends Panel {
    public I18nStringEditor(String id) {
        super(id);
        add(new TextField<>("text", new IModel<String>() {

            @Override
            public String getObject() {
                DbI18nString dbI18nString = ((DbI18nString) getDefaultModelObject());
                if (dbI18nString != null) {
                    return dbI18nString.getString();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(String string) {
                DbI18nString dbI18nString = ((DbI18nString) getDefaultModelObject());
                if (dbI18nString != null) {
                    dbI18nString.putString(string);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
        add(new Label("id", new AbstractReadOnlyModel<Integer>() {

            @Override
            public Integer getObject() {
                DbI18nString dbI18nString = ((DbI18nString) getDefaultModelObject());
                if (dbI18nString != null) {
                    return dbI18nString.getId();
                } else {
                    return null;
                }
            }
        }));
    }
}
