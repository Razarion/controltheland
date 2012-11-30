package com.btxtech.game.jsre.imagespritemapeditor;

import java.util.List;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class ImageSpriteMapEditorModel {
    private ImageSpriteMapInfo imageSpriteMapInfo;
    private ImageSpriteMapEditorGui imageSpriteMapEditorGui;
    private String[] overriddenImages;
    private ImageSpriteMapAccessAsync imageSpriteMapAccessAsync = GWT.create(ImageSpriteMapAccess.class);

    public void setFrameCount(int frameCount) {
        if (imageSpriteMapInfo.getFrameCount() != frameCount) {
            imageSpriteMapInfo.setFrameCount(frameCount);
            adjustOverridenImageArray();
            imageSpriteMapEditorGui.update();
        }
    }

    public void setFrameTime(int frameTime) {
        imageSpriteMapInfo.setFrameTime(frameTime);
        imageSpriteMapEditorGui.update();
    }

    public void setImageSpriteMapEditorGui(ImageSpriteMapEditorGui imageSpriteMapEditorGui) {
        this.imageSpriteMapEditorGui = imageSpriteMapEditorGui;
    }

    public ImageSpriteMapInfo getImageSpriteMapInfo() {
        return imageSpriteMapInfo;
    }

    private void adjustOverridenImageArray() {
        String[] tmpOverriddenImages = new String[imageSpriteMapInfo.getFrameCount()];
        if (overriddenImages != null) {
            for (int i = 0; i < tmpOverriddenImages.length; i++) {
                if (i < overriddenImages.length) {
                    tmpOverriddenImages[i] = overriddenImages[i];
                }
            }
        }
        overriddenImages = tmpOverriddenImages;
    }

    public boolean isFrameOverriden(int frame) {
        return overriddenImages != null && frame < overriddenImages.length && overriddenImages[frame] != null;
    }
    
    public void overrideImages(int frame, List<String> base64ImageDatas) {
        int totalSize = frame + base64ImageDatas.size();
        if (totalSize > overriddenImages.length) {
            setFrameCount(totalSize);
        }
        for (int i = frame; i < totalSize; i++) {
            overriddenImages[i] = base64ImageDatas.get(i - frame);
        }
        overrideImageSize(base64ImageDatas);
        imageSpriteMapEditorGui.update();
    }

    public String getOverriddenImage(int frame) {
        return overriddenImages[frame];
    }

    private void overrideImageSize(List<String> base64ImageDatas) {
        if (base64ImageDatas.size() > 0) {
            final Image image = new Image(base64ImageDatas.get(0));
            if (image.getWidth() == 0 || image.getHeight() == 0) {
                image.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        imageSpriteMapInfo.setFrameWidth(image.getWidth());
                        imageSpriteMapInfo.setFrameHeight(image.getHeight());
                        RootPanel.get().remove(image);
                        imageSpriteMapEditorGui.update();
                    }
                });
                RootPanel.get().add(image);
            } else {
                imageSpriteMapInfo.setFrameWidth(image.getWidth());
                imageSpriteMapInfo.setFrameHeight(image.getHeight());
            }
        }
    }

    private void onImageSpriteMapInfoLoaded(ImageSpriteMapInfo imageSpriteMapInfo) {
        this.imageSpriteMapInfo = imageSpriteMapInfo;
        adjustOverridenImageArray();
        imageSpriteMapEditorGui.update();
    }

    public void load(int imageSpriteMapId) {
        imageSpriteMapAccessAsync.loadImageSpriteMapInfo(imageSpriteMapId, new AsyncCallback<ImageSpriteMapInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                ClientExceptionHandler.handleException(caught);
            }

            @Override
            public void onSuccess(ImageSpriteMapInfo result) {
                onImageSpriteMapInfoLoaded(result);
            }
        });
    }
    
    public void save(final HasEnabled hasEnabled) {
        hasEnabled.setEnabled(false);
        imageSpriteMapAccessAsync.saveImageSpriteMapInfo(imageSpriteMapInfo, overriddenImages, new AsyncCallback<Void>(){
            @Override
            public void onFailure(Throwable caught) {
                ClientExceptionHandler.handleException(caught);
                hasEnabled.setEnabled(true);
            }

            @Override
            public void onSuccess(Void result) {
                hasEnabled.setEnabled(true);
            }
        });
    }
}
