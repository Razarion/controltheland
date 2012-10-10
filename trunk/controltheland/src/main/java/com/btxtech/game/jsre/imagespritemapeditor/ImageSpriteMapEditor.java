package com.btxtech.game.jsre.imagespritemapeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class ImageSpriteMapEditor implements EntryPoint {

    @Override
    public void onModuleLoad() {
        GwtCommon.setUncaughtExceptionHandler();
        int imageSpriteMapId = Integer.parseInt(Window.Location.getParameter(ImageSpriteMapAccessAsync.IMAGE_SPRITE_MAP_ID));

        ImageSpriteMapEditorGui imageSpriteMapEditorGui = new ImageSpriteMapEditorGui();
        RootPanel.get().add(imageSpriteMapEditorGui);

        ImageSpriteMapEditorModel imageSpriteMapEditorModel = new ImageSpriteMapEditorModel();
        imageSpriteMapEditorGui.setImageSpriteMapEditorModel(imageSpriteMapEditorModel);
        imageSpriteMapEditorModel.setImageSpriteMapEditorGui(imageSpriteMapEditorGui);
        imageSpriteMapEditorModel.load(imageSpriteMapId);
    }

}
