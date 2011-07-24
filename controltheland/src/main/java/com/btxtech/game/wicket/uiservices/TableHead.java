package com.btxtech.game.wicket.uiservices;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import java.util.List;


/**
 * User: beat
 * Date: 23.07.2011
 * Time: 11:54:22
 */
public class TableHead extends Panel {
    private List<String> columnNames;

    public TableHead(String id, List<String> columnNames, String cssClassHead) {
        super(id);
        this.columnNames = columnNames;
        if (columnNames == null) {
            return;
        }
        RepeatingView view = new RepeatingView("label");
        for (String columnName : columnNames) {
            view.add(new Label(view.newChildId(), columnName));
        }
        add(view);
        if (cssClassHead != null) {
            add(new SimpleAttributeModifier("class", cssClassHead));
        }
    }

    @Override
    public boolean isVisible() {
        return columnNames != null;
    }
}
