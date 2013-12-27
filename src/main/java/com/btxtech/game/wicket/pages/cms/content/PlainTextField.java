package com.btxtech.game.wicket.pages.cms.content;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 11.05.12
 * Time: 20:08
 */
public class PlainTextField extends Panel {
    public PlainTextField(String id, IModel model) {
        super(id, model);
        add(new TextField<>("field", new IModel<String>() {

            @Override
            public String getObject() {
                return (String) getDefaultModelObject();
            }

            @Override
            public void setObject(String html) {
                setDefaultModelObject(html);
            }

            @Override
            public void detach() {
            }
        }));
    }
}
