package com.btxtech.game.jsre.itemtypeeditor;

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
        // Setup common
        // GwtCommon.setUncaughtExceptionHandler();
        // GwtCommon.disableBrowserContextMenuJSNI();
        log.log(Level.SEVERE, "Start ItemTypeEditor");
        try {
            RootPanel rootPanel = RootPanel.get(ITEM_TYPE_EDITOR);
            Element element = rootPanel.getElement();
            element.removeChild(element.getChild(0));
            String idString = rootPanel.getElement().getAttribute(ITEM_TYPE_ID);
            int itemTypeId = Integer.parseInt(idString);
            log.log(Level.SEVERE, "Id: " + itemTypeId);
            rootPanel.add(new ItemTypeEditorPanel(itemTypeId));
        } catch (Throwable throwable) {
            log.log(Level.SEVERE, "Error: ", throwable);
        }
    }
}
