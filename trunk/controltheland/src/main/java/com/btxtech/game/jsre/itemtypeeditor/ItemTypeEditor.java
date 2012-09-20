package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.terrain.TerrainView;
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
        TerrainView.uglySuppressRadar = true;
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();
        try {
            RootPanel rootPanel = RootPanel.get(ITEM_TYPE_EDITOR);
            removeLoadingText(rootPanel);
            rootPanel.add(new ItemTypeEditorPanel());
            ItemTypeEditorModel.getInstance().loadItemType(getItemTypeId(rootPanel));
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
