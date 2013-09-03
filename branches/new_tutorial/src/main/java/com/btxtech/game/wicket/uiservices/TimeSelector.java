package com.btxtech.game.wicket.uiservices;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 14.08.2011
 * Time: 19:43:18
 */
public class TimeSelector extends Panel {
    public static enum Converter {
        MINUTES_TO_MS {

            @Override
            public Long toUserValue(Long modelValue) {
                if (modelValue != null) {
                    return modelValue / 1000 / 60;
                } else {
                    return null;
                }
            }
            @Override
            public Object toModelValue(Long userValue) {
                if (userValue != null) {
                    return userValue * 1000 * 60;
                } else {
                    return null;
                }
            }};

        public abstract Long toUserValue(Long modelValue);

        public abstract Object toModelValue(Long userValue);
    }

    public TimeSelector(String id, final Converter converter) {
        super(id);

        add(new TextField<Long>("value", new IModel<Long>() {

            @Override
            public Long getObject() {
                return converter.toUserValue((Long) getDefaultModelObject());
            }

            @Override
            public void setObject(Long value) {
                setDefaultModelObject(converter.toModelValue(value));
            }

            @Override
            public void detach() {
            }
        }, Long.class));
    }
}
