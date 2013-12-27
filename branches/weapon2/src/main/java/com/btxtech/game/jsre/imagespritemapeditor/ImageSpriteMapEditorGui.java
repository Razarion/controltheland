package com.btxtech.game.jsre.imagespritemapeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageSpriteMapEditorGui extends Composite {

    private static ImageSpriteMapEditorGuiUiBinder uiBinder = GWT.create(ImageSpriteMapEditorGuiUiBinder.class);
    @UiField
    InlineLabel widthLabel;
    @UiField
    InlineLabel heightLabel;
    @UiField
    IntegerBox countBox;
    @UiField
    IntegerBox timeBox;
    @UiField
    FlexTable imageTable;
    @UiField
    SimplePanel moviePanel;
    @UiField
    Button applyCountButton;
    @UiField
    Button playButton;
    @UiField
    Button stopButton;
    @UiField
    SimpleCheckBox loopCheckbox;
    @UiField
    Button saveButton;
    private ImageSpriteMapEditorModel imageSpriteMapEditorModel;
    private ImageSpriteMapEditorRenderer spriteMapEditorRenderer;

    interface ImageSpriteMapEditorGuiUiBinder extends UiBinder<Widget, ImageSpriteMapEditorGui> {
    }

    public ImageSpriteMapEditorGui() {
        initWidget(uiBinder.createAndBindUi(this));
        spriteMapEditorRenderer = new ImageSpriteMapEditorRenderer();
        moviePanel.setWidget(spriteMapEditorRenderer.getCanvas());
    }

    public void setImageSpriteMapEditorModel(ImageSpriteMapEditorModel imageSpriteMapEditorModel) {
        this.imageSpriteMapEditorModel = imageSpriteMapEditorModel;
        spriteMapEditorRenderer.setImageSpriteMapEditorModel(imageSpriteMapEditorModel);
    }

    public void update() {
        if (imageSpriteMapEditorModel.getImageSpriteMapInfo() == null) {
            return;
        }
        spriteMapEditorRenderer.updateSize(imageSpriteMapEditorModel.getImageSpriteMapInfo().getFrameWidth(), imageSpriteMapEditorModel.getImageSpriteMapInfo().getFrameHeight());
        spriteMapEditorRenderer.play();
        widthLabel.setText(Integer.toString(imageSpriteMapEditorModel.getImageSpriteMapInfo().getFrameWidth()));
        heightLabel.setText(Integer.toString(imageSpriteMapEditorModel.getImageSpriteMapInfo().getFrameHeight()));
        countBox.setValue(imageSpriteMapEditorModel.getImageSpriteMapInfo().getFrameCount());
        timeBox.setValue(imageSpriteMapEditorModel.getImageSpriteMapInfo().getFrameTime());
        imageTable.removeAllRows();
        for (int frame = 0; frame < imageSpriteMapEditorModel.getImageSpriteMapInfo().getFrameCount(); frame++) {
            imageTable.setWidget(frame, 0, new ImagePanel(frame, imageSpriteMapEditorModel));
        }
    }

    @UiHandler("applyCountButton")
    void onApplyCountButtonClick(ClickEvent event) {
        imageSpriteMapEditorModel.setFrameCount(countBox.getValue());
    }

    @UiHandler("timeBox")
    void onTimeBoxChange(ChangeEvent event) {
        imageSpriteMapEditorModel.setFrameTime(timeBox.getValue());
    }

    @UiHandler("timeBox")
    void onTimeBoxKeyUp(KeyUpEvent event) {
        imageSpriteMapEditorModel.setFrameTime(timeBox.getValue());
    }

    @UiHandler("playButton")
    void onPlayButtonClick(ClickEvent event) {
        spriteMapEditorRenderer.play();
    }

    @UiHandler("stopButton")
    void onStopButtonClick(ClickEvent event) {
        spriteMapEditorRenderer.stop();
    }

    @UiHandler("loopCheckbox")
    void onLoopCheckboxClick(ClickEvent event) {
        spriteMapEditorRenderer.setLoop(loopCheckbox.getValue());
    }

    @UiHandler("saveButton")
    void onSaveButtonClick(ClickEvent event) {
        imageSpriteMapEditorModel.save(saveButton);
    }
}
