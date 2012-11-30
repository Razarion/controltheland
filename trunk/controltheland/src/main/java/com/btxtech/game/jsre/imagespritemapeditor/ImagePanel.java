package com.btxtech.game.jsre.imagespritemapeditor;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public class ImagePanel extends Composite implements DropHandler, DragOverHandler, DragEnterHandler {

    private static ImagePanelUiBinder uiBinder = GWT.create(ImagePanelUiBinder.class);
    @UiField
    Label noImageLabel;
    @UiField(provided = true)
    Image serverImage;
    @UiField
    Image clientImage;
    private int frame;
    private List<String> base64ImageDatas;
    private Integer expectedDropCount;
    private ImageSpriteMapEditorModel imageSpriteMapEditorModel;

    interface ImagePanelUiBinder extends UiBinder<Widget, ImagePanel> {
    }

    public ImagePanel(int frame, ImageSpriteMapEditorModel imageSpriteMapEditorModel) {
        ImageSpriteMapInfo imageSpriteMapInfo = imageSpriteMapEditorModel.getImageSpriteMapInfo();
        Index offset = imageSpriteMapInfo.getSpriteMapOffset(frame);
        serverImage = new Image(ImageHandler.getImageSpriteMapUrl(imageSpriteMapInfo.getId()), offset.getX(), offset.getY(), imageSpriteMapInfo.getFrameWidth(), imageSpriteMapInfo.getFrameHeight());
        initWidget(uiBinder.createAndBindUi(this));
        this.frame = frame;
        this.imageSpriteMapEditorModel = imageSpriteMapEditorModel;
        if (imageSpriteMapEditorModel.isFrameOverriden(frame)) {
            clientImage.setVisible(true);
            clientImage.setUrl(imageSpriteMapEditorModel.getOverriddenImage(frame));
            clientImage.addDropHandler(this);
            clientImage.addDragOverHandler(this);
            clientImage.addDragEnterHandler(this);
        } else {
            if (imageSpriteMapInfo.getFrameWidth() > 0 && imageSpriteMapInfo.getFrameWidth() > 0) {
                serverImage.addDropHandler(this);
                serverImage.addDragOverHandler(this);
                serverImage.addDragEnterHandler(this);
                serverImage.setVisible(true);
            } else {
                noImageLabel.addDropHandler(this);
                noImageLabel.addDragOverHandler(this);
                noImageLabel.addDragEnterHandler(this);
                noImageLabel.setVisible(true);
            }
        }
    }

    private void startDrop(DropEvent event) {
        base64ImageDatas = new ArrayList<String>();
        DataTransfer dataTransfer = event.getDataTransfer();
        expectedDropCount = evaluateDroppedData(dataTransfer, ImagePanel.this);
    }

    private native int evaluateDroppedData(DataTransfer dataTransfer, ImagePanel callback) /*-{
        var imageCount = 0;
        for (var i = 0; i < dataTransfer.files.length; i++) {
            var file = dataTransfer.files[i];
            if (!file.type.match('image.*')) {
                continue;
            }
            var reader = new FileReader();
            imageCount++;
            // Closure to capture the file information.
            reader.onload = (function (theFile) {
                return function (e) {
                    $entry(@com.btxtech.game.jsre.imagespritemapeditor.ImagePanel::staticLoadCallback(Ljava/lang/String;Lcom/btxtech/game/jsre/imagespritemapeditor/ImagePanel;)(e.target.result, callback));
                };
            })(file);
            reader.readAsDataURL(file);
        }
        return imageCount;
    }-*/;

    private static void staticLoadCallback(String base64ImageData, ImagePanel callback) {
        try {
            callback.loadCallback(base64ImageData);
        } catch (Exception e) {
            ClientExceptionHandler.handleException(e);
        }
    }

    private void loadCallback(String base64ImageData) {
        if (expectedDropCount == null) {
            throw new IllegalStateException("DropImage.loadCallback() expectedDropCount == null");
        }
        base64ImageDatas.add(base64ImageData);
        expectedDropCount -= 1;
        if (expectedDropCount == 0) {
            expectedDropCount = null;
            finalizeDrop();
        }
    }

    private void finalizeDrop() {
        if (base64ImageDatas != null) {
            imageSpriteMapEditorModel.overrideImages(frame, base64ImageDatas);
            base64ImageDatas = null;
        }
    }

    @Override
    public void onDrop(final DropEvent event) {
        event.stopPropagation();
        event.preventDefault();
        startDrop(event);
    }

    @Override
    public void onDragOver(DragOverEvent event) {
        event.stopPropagation();
        event.preventDefault();
    }

    @Override
    public void onDragEnter(DragEnterEvent event) {
        event.stopPropagation();
        event.preventDefault();
    }

    public HandlerRegistration addDropHandler(DropHandler handler) {
        return addBitlessDomHandler(handler, DropEvent.getType());
    }

    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return addBitlessDomHandler(handler, DragOverEvent.getType());
    }

    public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
        return addBitlessDomHandler(handler, DragEnterEvent.getType());
    }

}
