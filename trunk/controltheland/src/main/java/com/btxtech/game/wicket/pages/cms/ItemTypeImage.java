package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 21:07:07
 */
public class ItemTypeImage extends Panel {
    private int imgId;
    private int imgIndex;

    public ItemTypeImage(String id, DbItemType dbItemType) {
        super(id);
        imgId = dbItemType.getId();
        imgIndex = dbItemType.getBoundingBox().getCosmeticImageIndex();
        add(new Image());
    }

    private class Image extends MarkupContainer {

        public Image() {
            super("image");
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            checkComponentTag(tag, "img");
            tag.put("src", ImageHandler.getItemTYpeUrl(imgId, imgIndex));
        }

    }
}
