package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MuzzleFlashPanel extends Composite implements ItemTypeEditorModel.UpdateListener {

    private static MuzzleFlashPanelUiBinder uiBinder = GWT.create(MuzzleFlashPanelUiBinder.class);
    @UiField
    Label rotationStepLabel;
    @UiField
    Button rotationStepLeftButton;
    @UiField
    Button rotationStepRightButton;
    @UiField
    IntegerBox muzzleFlashCountField;
    @UiField
    Button muzzleFlashCountIncrease;
    @UiField
    Button muzzleFlashCountDecrease;
    @UiField
    Label muzzleFlashEditLabel;
    @UiField
    Button muzzleFlashEditPlusButton;
    @UiField
    Button muzzleFlashEditMinusButton;
    @UiField
    IntegerBox muzzleFlashXField;
    @UiField
    Button muzzleFlashXPlusButton;
    @UiField
    Button muzzleFlashXMinusButton;
    @UiField
    IntegerBox muzzleFlashYField;
    @UiField
    Button muzzleFlashYPlusButton;
    @UiField
    Button muzzleFlashYMinusButton;
    @UiField
    SimplePanel muzzleFlashVisualisationPanel;
    private int muzzleFlashNumber;

    interface MuzzleFlashPanelUiBinder extends UiBinder<Widget, MuzzleFlashPanel> {
    }

    public MuzzleFlashPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        muzzleFlashNumber = 0;
        MuzzleFlashVisualisation muzzleFlashVisualisation = new MuzzleFlashVisualisation(this);
        muzzleFlashVisualisationPanel.setWidget(muzzleFlashVisualisation.getCanvas());
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
        ItemTypeEditorModel.getInstance().addUpdateListener(muzzleFlashVisualisation);
    }

    @UiHandler("rotationStepLeftButton")
    void onRotationStepLeftButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().increaseCurrentAngelIndex();
    }

    @UiHandler("rotationStepRightButton")
    void onRotationStepRightButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().decreaseCurrentAngelIndex();
    }

    @UiHandler("muzzleFlashCountField")
    void onMuzzleFlashCountFieldChange(ChangeEvent event) {
        setMuzzleFlashCount(muzzleFlashCountField.getValue());
    }

    private void setMuzzleFlashCount(int count) {
        if (count < 1) {
            return;
        }
        if (muzzleFlashNumber >= count) {
            muzzleFlashNumber = count - 1;
        }
        ItemTypeEditorModel.getInstance().getWeaponType().changeMuzzleFlashCount(count);
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("muzzleFlashCountIncrease")
    void onMuzzleFlashCountIncreaseClick(ClickEvent event) {
        setMuzzleFlashCount(muzzleFlashCountField.getValue() + 1);
    }

    @UiHandler("muzzleFlashCountDecrease")
    void onMuzzleFlashCountDecreaseClick(ClickEvent event) {
        setMuzzleFlashCount(muzzleFlashCountField.getValue() - 1);
    }

    @UiHandler("muzzleFlashEditPlusButton")
    void onMuzzleFlashEditPlusButtonClick(ClickEvent event) {
        setMuzzleFlashNumber(muzzleFlashNumber + 1);
    }

    @UiHandler("muzzleFlashEditMinusButton")
    void onMuzzleFlashEditMinusButtonClick(ClickEvent event) {
        setMuzzleFlashNumber(muzzleFlashNumber - 1);
    }

    private void setMuzzleFlashNumber(int count) {
        muzzleFlashNumber = count;
        if (muzzleFlashNumber >= ItemTypeEditorModel.getInstance().getWeaponType().getMuzzleFlashCount()) {
            muzzleFlashNumber = ItemTypeEditorModel.getInstance().getWeaponType().getMuzzleFlashCount() - 1;
        }
        if (muzzleFlashNumber < 0) {
            muzzleFlashNumber = 0;
        }
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("muzzleFlashXField")
    void onMuzzleFlashXEditFieldChange(ChangeEvent event) {
        setMuzzleFlashX(muzzleFlashXField.getValue());
    }

    @UiHandler("muzzleFlashXPlusButton")
    void onMuzzleFlashXPlusButtonClick(ClickEvent event) {
        setMuzzleFlashX(muzzleFlashXField.getValue() + 1);
    }

    @UiHandler("muzzleFlashXMinusButton")
    void onMuzzleFlashXMinusButtonClick(ClickEvent event) {
        setMuzzleFlashX(muzzleFlashXField.getValue() - 1);
    }

    private void setMuzzleFlashX(int xValue) {
        int y = ItemTypeEditorModel.getInstance().getWeaponType().getMuzzleFlashPosition(muzzleFlashNumber, ItemTypeEditorModel.getInstance().getCurrentAngelIndex()).getY();
        ItemTypeEditorModel.getInstance().getWeaponType().setMuzzleFlashPosition(muzzleFlashNumber,
                ItemTypeEditorModel.getInstance().getCurrentAngelIndex(),
                new Index(xValue, y));
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("muzzleFlashYField")
    void onMuzzleFlashYFieldChange(ChangeEvent event) {
        setMuzzleFlashY(muzzleFlashYField.getValue());
    }

    @UiHandler("muzzleFlashYPlusButton")
    void onMuzzleFlashYPlusButtonClick(ClickEvent event) {
        setMuzzleFlashY(muzzleFlashYField.getValue() + 1);
    }

    @UiHandler("muzzleFlashYMinusButton")
    void onMuzzleFlashYMinusButtonClick(ClickEvent event) {
        setMuzzleFlashY(muzzleFlashYField.getValue() - 1);
    }

    private void setMuzzleFlashY(int yValue) {
        int x = ItemTypeEditorModel.getInstance().getWeaponType().getMuzzleFlashPosition(muzzleFlashNumber, ItemTypeEditorModel.getInstance().getCurrentAngelIndex()).getX();
        ItemTypeEditorModel.getInstance().getWeaponType().setMuzzleFlashPosition(muzzleFlashNumber,
                ItemTypeEditorModel.getInstance().getCurrentAngelIndex(),
                new Index(x, yValue));
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @Override
    public void onModelUpdate() {
        if (ItemTypeEditorModel.getInstance().getWeaponType() != null) {
            rotationStepLabel.setText((ItemTypeEditorModel.getInstance().getCurrentAngelIndex() + 1) + " of " + (ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount()));
            muzzleFlashEditLabel.setText(Integer.toString(muzzleFlashNumber + 1));
            muzzleFlashCountField.setValue(ItemTypeEditorModel.getInstance().getWeaponType().getMuzzleFlashCount());
            Index muzzlePos = ItemTypeEditorModel.getInstance().getWeaponType().getMuzzleFlashPosition(muzzleFlashNumber, ItemTypeEditorModel.getInstance().getCurrentAngelIndex());
            muzzleFlashXField.setValue(muzzlePos.getX());
            muzzleFlashYField.setValue(muzzlePos.getY());
        } else {
            rotationStepLeftButton.setEnabled(false);
            rotationStepRightButton.setEnabled(false);
            muzzleFlashCountField.setEnabled(false);
            muzzleFlashCountIncrease.setEnabled(false);
            muzzleFlashCountDecrease.setEnabled(false);
            muzzleFlashEditPlusButton.setEnabled(false);
            muzzleFlashEditMinusButton.setEnabled(false);
            muzzleFlashXField.setEnabled(false);
            muzzleFlashXPlusButton.setEnabled(false);
            muzzleFlashXMinusButton.setEnabled(false);
            muzzleFlashYField.setEnabled(false);
            muzzleFlashYPlusButton.setEnabled(false);
            muzzleFlashYMinusButton.setEnabled(false);
        }
    }

    public int getMuzzleFlashNumber() {
        return muzzleFlashNumber;
    }
}
