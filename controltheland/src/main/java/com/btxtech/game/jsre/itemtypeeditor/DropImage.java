package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public class DropImage extends Image {
    private Integer expectedDropCount;
    private List<String> base64ImageDatas;
    private Boolean dropFrames;
    private int angelIndex;
    private int step;
    private int frame;
    private ItemTypeSpriteMap.SyncObjectState syncObjectState;

    public DropImage(String url, int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        super(url);
        init(angelIndex, step, frame, syncObjectState);
    }

    public DropImage(String url, int left, int top, int width, int height, int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        super(url, left, top, width, height);
        init(angelIndex, step, frame, syncObjectState);
    }

    private void init(int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        this.angelIndex = angelIndex;
        this.step = step;
        this.frame = frame;
        this.syncObjectState = syncObjectState;
        getElement().getStyle().setProperty("minWidth", "20px");
        getElement().getStyle().setProperty("minHeight", "20px");
        getElement().getStyle().setBackgroundColor("#CCCCCC");
        addDropHandler(new DropHandler() {
            @Override
            public void onDrop(final DropEvent event) {
                event.stopPropagation();
                event.preventDefault();
                startDrop(event);
                final DecoratedPopupPanel simplePopup = new DecoratedPopupPanel(true);
                VerticalPanel verticalPanel = new VerticalPanel();
                verticalPanel.add(new Button("Angels/Steps", new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent ignore) {
                        dropFrames = false;
                        simplePopup.hide();
                        finalizeDrop();
                    }

                }));
                verticalPanel.add(new Button("Frames", new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent ignore) {
                        dropFrames = true;
                        simplePopup.hide();
                        finalizeDrop();
                    }

                }));
                simplePopup.setWidget(verticalPanel);
                simplePopup.getElement().getStyle().setBackgroundColor("#FF0000");
                Widget source = (Widget) event.getSource();
                int left = source.getAbsoluteLeft() + 10;
                int top = source.getAbsoluteTop() + 10;
                simplePopup.setPopupPosition(left, top);
                simplePopup.show();
            }
        });
        addDragOverHandler(new DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        });
        addDragEnterHandler(new DragEnterHandler() {
            @Override
            public void onDragEnter(DragEnterEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        });
    }

    private void startDrop(DropEvent event) {
        base64ImageDatas = new ArrayList<String>();
        DataTransfer dataTransfer = event.getDataTransfer();
        expectedDropCount = evaluateDroppedData(dataTransfer, DropImage.this);
    }

    private native int evaluateDroppedData(DataTransfer dataTransfer, DropImage callback) /*-{
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
                    $entry(@com.btxtech.game.jsre.itemtypeeditor.DropImage::staticLoadCallback(Ljava/lang/String;Lcom/btxtech/game/jsre/itemtypeeditor/DropImage;)(e.target.result, callback));
                };
            })(file);
            reader.readAsDataURL(file);
        }
        return imageCount;
    }-*/;

    private static void staticLoadCallback(String base64ImageData, DropImage callback) {
        callback.loadCallback(base64ImageData);
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
        if (base64ImageDatas != null && dropFrames != null) {
            ItemTypeEditorModel.getInstance().overrideImages(dropFrames, angelIndex, step, frame, syncObjectState, base64ImageDatas);
            base64ImageDatas = null;
            dropFrames = null;
        }
    }
}
