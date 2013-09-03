package com.btxtech.game.jsre.regioneditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class ButtonRegionEditorControl extends Composite {
    private static ButtonRegionEditorControlUiBinder uiBinder = GWT.create(ButtonRegionEditorControlUiBinder.class);
    @UiField
    PushButton paint;
    @UiField
    PushButton erase;
    @UiField
    PushButton cursorSmall;
    @UiField
    PushButton cursorMiddle;
    @UiField
    PushButton cursorBig;
    @UiField
    PushButton zoomIn;
    @UiField
    PushButton zoomOut;
    @UiField
    PushButton save;
    private RegionEditorModel regionEditorModel;

    interface ButtonRegionEditorControlUiBinder extends UiBinder<Widget, ButtonRegionEditorControl> {
    }

    public ButtonRegionEditorControl(RegionEditorModel regionEditorModel) {
        this.regionEditorModel = regionEditorModel;
        initWidget(uiBinder.createAndBindUi(this));
        setDefaultMode();
    }

    private void setDefaultMode() {
        paint.getElement().getStyle().setBorderColor("#000000");
        paint.getElement().getStyle().setBorderWidth(3, Style.Unit.PX);
        regionEditorModel.setMode(RegionEditorModel.Mode.PAINT);
        cursorSmall.getElement().getStyle().setBorderColor("#000000");
        cursorSmall.getElement().getStyle().setBorderWidth(3, Style.Unit.PX);
        regionEditorModel.setCursorSize(RegionEditorModel.CursorSize.SMALL);
    }

    @UiHandler("paint")
    void onPaintButtonClick(ClickEvent event) {
        paint.getElement().getStyle().setBorderColor("#000000");
        paint.getElement().getStyle().setBorderWidth(3, Style.Unit.PX);
        erase.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        regionEditorModel.setMode(RegionEditorModel.Mode.PAINT);
    }

    @UiHandler("erase")
    void onEraseButtonClick(ClickEvent event) {
        erase.getElement().getStyle().setBorderColor("#000000");
        erase.getElement().getStyle().setBorderWidth(3, Style.Unit.PX);
        paint.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        regionEditorModel.setMode(RegionEditorModel.Mode.ERASE);
    }

    @UiHandler("cursorSmall")
    void onCursorSmallButtonClick(ClickEvent event) {
        cursorSmall.getElement().getStyle().setBorderColor("#000000");
        cursorSmall.getElement().getStyle().setBorderWidth(3, Style.Unit.PX);
        cursorMiddle.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        cursorBig.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        regionEditorModel.setCursorSize(RegionEditorModel.CursorSize.SMALL);
    }

    @UiHandler("cursorMiddle")
    void onCursorMiddleButtonClick(ClickEvent event) {
        cursorMiddle.getElement().getStyle().setBorderColor("#000000");
        cursorMiddle.getElement().getStyle().setBorderWidth(3, Style.Unit.PX);
        cursorSmall.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        cursorBig.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        regionEditorModel.setCursorSize(RegionEditorModel.CursorSize.MIDDLE);
    }

    @UiHandler("cursorBig")
    void onCursorBigButtonClick(ClickEvent event) {
        cursorBig.getElement().getStyle().setBorderColor("#000000");
        cursorBig.getElement().getStyle().setBorderWidth(3, Style.Unit.PX);
        cursorSmall.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        cursorMiddle.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        regionEditorModel.setCursorSize(RegionEditorModel.CursorSize.BIG);
    }

    @UiHandler("zoomIn")
    void onZoomInButtonClick(ClickEvent event) {
        regionEditorModel.zoomIn();
    }

    @UiHandler("zoomOut")
    void onZoomOutButtonClick(ClickEvent event) {
        regionEditorModel.zoomOut();
    }

    @UiHandler("save")
    void onSaveButtonClick(ClickEvent event) {
        save.setEnabled(false);
        regionEditorModel.save(save);
    }

}
