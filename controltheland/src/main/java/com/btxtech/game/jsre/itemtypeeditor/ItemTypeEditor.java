package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 15.08.2011
 * Time: 15:04:57
 */
public class ItemTypeEditor implements EntryPoint {
    public static final String ITEM_TYPE_EDITOR = "ItemTypeEditor";
    public static final String ITEM_TYPE_ID = "itemTypeId";
    private Logger log = Logger.getLogger(ItemTypeEditor.class.getName());

    @Override
    public void onModuleLoad() {
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();
        log.log(Level.SEVERE, "Start ItemTypeEditor");
        try {
            RootPanel rootPanel = RootPanel.get(ITEM_TYPE_EDITOR);
            removeLoadingText(rootPanel);
            int itemTypeId = getItemTypeId(rootPanel);
            log.log(Level.SEVERE, "Id: " + itemTypeId);
            rootPanel.add(new ItemTypeEditorPanel(itemTypeId));
        } catch (Throwable throwable) {
            log.log(Level.SEVERE, "Error: ", throwable);
        }
    }

    private int getItemTypeId(RootPanel rootPanel) {
        String idString = rootPanel.getElement().getAttribute(ITEM_TYPE_ID);
        return Integer.parseInt(idString);
    }

    private void removeLoadingText(RootPanel rootPanel) {
        Element element = rootPanel.getElement();
        element.removeChild(element.getChild(0));
    }
}
