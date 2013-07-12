package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.item.itemType.DbItemType;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 21:07:07
 */
public class ItemTypeImage extends Panel {
    public ItemTypeImage(String id, DbItemType dbItemType, DbExpressionProperty dbExpressionProperty) {
        super(id);
        Image image = CmsItemTypeImageResource.createImage("image", dbItemType);
        if (dbExpressionProperty.getCssClass() != null) {
            image.add(new AttributeModifier("class", dbExpressionProperty.getCssClass()));
        }
        add(image);
    }
}
