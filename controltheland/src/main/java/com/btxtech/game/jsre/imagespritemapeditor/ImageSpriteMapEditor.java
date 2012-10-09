package com.btxtech.game.jsre.imagespritemapeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class ImageSpriteMapEditor implements EntryPoint {

    @Override
    public void onModuleLoad() {
        GwtCommon.setUncaughtExceptionHandler();

        ImageSpriteMapEditorGui imageSpriteMapEditorGui = new ImageSpriteMapEditorGui();
        RootPanel.get().add(imageSpriteMapEditorGui);

        ImageSpriteMapEditorModel imageSpriteMapEditorModel = new ImageSpriteMapEditorModel();
        imageSpriteMapEditorGui.setImageSpriteMapEditorModel(imageSpriteMapEditorModel);
        imageSpriteMapEditorModel.setImageSpriteMapEditorGui(imageSpriteMapEditorGui);
        imageSpriteMapEditorModel.load();
    }

}
