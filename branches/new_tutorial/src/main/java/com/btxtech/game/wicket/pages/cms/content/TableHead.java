package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;


/**
 * User: beat
 * Date: 23.07.2011
 * Time: 11:54:22
 */
public class TableHead extends Panel {
    private boolean visible;

    public TableHead(String id, DbContentList dbContentList, String cssClassHead, BeanIdPathElement beanIdPathElement, ContentContext contentContext) {
        super(id);

        if (!dbContentList.isShowHead()) {
            visible = false;
            return;
        }
        visible = true;

        RepeatingView view = new RepeatingView("cell");
        if (!contentContext.isSorting(dbContentList.getId())) {
            contentContext.setDefaultSort(dbContentList);
        }
        for (DbContent dbContent : dbContentList.getColumnsCrud().readDbChildren()) {
            String name = dbContent.getName();
            if (dbContent instanceof DbExpressionProperty && ((DbExpressionProperty) dbContent).isSortable()) {
                view.add(new TableHeadCell(view.newChildId(), dbContentList.getId(), (DbExpressionProperty) dbContent, beanIdPathElement, contentContext));
            } else {
                view.add(new Label(view.newChildId(), name));
            }
        }
        add(view);
        if (cssClassHead != null) {
            add(new AttributeModifier("class", cssClassHead));
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}
