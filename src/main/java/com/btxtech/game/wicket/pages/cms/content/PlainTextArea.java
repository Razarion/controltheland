package com.btxtech.game.wicket.pages.cms.content;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 11.05.12
 * Time: 20:08
 */
public class PlainTextArea extends Panel {
    public PlainTextArea(String id, IModel model) {
        super(id, model);
        add(new TextArea<>("textArea", new IModel<String>() {

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
