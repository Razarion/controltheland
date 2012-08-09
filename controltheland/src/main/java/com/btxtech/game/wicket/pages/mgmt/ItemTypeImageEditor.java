package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.itemtypeeditor.ItemTypeEditor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;

/**
 * User: beat
 * Date: 11.09.2011
 * Time: 22:10:58
 */
public class ItemTypeImageEditor extends MgmtWebPage {
    public ItemTypeImageEditor(Integer itemTypeId) {
        Label gwtItemEditor = new Label("itemTypeEditor", "<DIV>Loading Item Type Editor</DIV>");
        gwtItemEditor.setEscapeModelStrings(false);
        gwtItemEditor.add(new SimpleAttributeModifier("id", ItemTypeEditor.ITEM_TYPE_EDITOR));
        gwtItemEditor.add(new SimpleAttributeModifier(ItemTypeEditor.ITEM_TYPE_ID, Integer.toString(itemTypeId)));
        add(gwtItemEditor);
        add(JavascriptPackageResource.getHeaderContribution("itemtypeeditor/itemtypeeditor.nocache.js"));
    }
}
