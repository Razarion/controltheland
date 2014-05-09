package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 05.08.12
 * Time: 13:25
 */
public class SimulationControlPanel extends Composite implements ItemTypeEditorModel.LoadedListener {
    private static SimulationControlPanelUiBinder uiBinder = GWT.create(SimulationControlPanelUiBinder.class);
    @UiField
    Button attackButton;
    @UiField
    Button moveButton;
    @UiField
    IntegerBox buildupField;
    @UiField
    Button buildupPlusButton;
    @UiField
    Button buildupMinusButton;
    @UiField
    IntegerBox demolitionField;
    @UiField
    Button demolitionPlusButton;
    @UiField
    Button demolitionMinusButton;
    @UiField
    IntegerBox resourceItemTypeIdField;
    @UiField
    Button harvestButton;
    @UiField
    Button surfaceButton;
    @UiField
    IntegerBox surfaceImageIdField;
    @UiField
    Button buildupButton;
    @UiField
    IntegerBox buildupItemTypeIdField;

    interface SimulationControlPanelUiBinder extends UiBinder<Widget, SimulationControlPanel> {
    }

    public SimulationControlPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addLoadedListener(this);
    }

    @UiHandler("attackButton")
    void onAttackButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().doAttack();
    }

    @UiHandler("moveButton")
    void onMoveButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().executeMoveCommand();
    }

    @UiHandler("buildupField")
    void onBuildupFieldChange(ChangeEvent event) {
        setBuildup(buildupField.getValue());
    }

    @UiHandler("buildupPlusButton")
    void onBuildupPlusButtonClick(ClickEvent event) {
        setBuildup(buildupField.getValue() + 1);
    }

    @UiHandler("buildupMinusButton")
    void onBuildupMinusButtonClick(ClickEvent event) {
        setBuildup(buildupField.getValue() - 1);
    }

    private void setBuildup(int buildup) {
        if (buildup > 100) {
            buildup = 100;
        }
        if (buildup < 0) {
            buildup = 0;
        }
        ItemTypeEditorModel.getInstance().setBaseItemTypeBuildup((double) buildup / 100.0);
        buildupField.setValue(buildup);
    }

    @UiHandler("demolitionField")
    void onDemolitionFieldChange(ChangeEvent event) {
        setDemolition(demolitionField.getValue());
    }

    @UiHandler("demolitionPlusButton")
    void onDemolitionPlusButtonClick(ClickEvent event) {
        setDemolition(demolitionField.getValue() + 1);
    }

    @UiHandler("demolitionMinusButton")
    void onDemolitionMinusButtonClick(ClickEvent event) {
        setDemolition(demolitionField.getValue() - 1);
    }

    @UiHandler("harvestButton")
    void onHarvestButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().doHarvest(resourceItemTypeIdField.getValue());
    }

    @UiHandler("buildupButton")
    void onBuildupButtonButton(ClickEvent event) {
        ItemTypeEditorModel.getInstance().doBuildup(buildupItemTypeIdField.getValue());
    }

    @UiHandler("surfaceButton")
    void onSurfaceButton(ClickEvent event) {
        ItemTypeEditorModel.getInstance().setupSurface(surfaceImageIdField.getValue());
    }

    private void setDemolition(int demolition) {
        if (demolition > 100) {
            demolition = 100;
        }
        if (demolition < 0) {
            demolition = 0;
        }
        ItemTypeEditorModel.getInstance().setBaseItemTypeDemolition((double) demolition / 100.0);
        demolitionField.setValue(demolition);
    }

    @Override
    public void onModelLoaded() {
        surfaceImageIdField.setValue(ItemTypeEditorModel.SURFACE_IMAGE_ID);
        if (ItemTypeEditorModel.getInstance().getSyncItem() instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) ItemTypeEditorModel.getInstance().getSyncItem();
            moveButton.setEnabled(syncBaseItem.hasSyncMovable());
            attackButton.setEnabled(syncBaseItem.hasSyncWeapon());
            buildupField.setValue((int) (syncBaseItem.getBuildup() * 100.0));
            demolitionField.setValue((int) (syncBaseItem.getNormalizedHealth() * 100.0));
            resourceItemTypeIdField.setValue(5);
            buildupItemTypeIdField.setValue(3);
        } else {
            moveButton.setEnabled(false);
            attackButton.setEnabled(false);
            buildupField.setEnabled(false);
            buildupPlusButton.setEnabled(false);
            buildupMinusButton.setEnabled(false);
            demolitionField.setEnabled(false);
            demolitionPlusButton.setEnabled(false);
            demolitionMinusButton.setEnabled(false);
            resourceItemTypeIdField.setEnabled(false);
            harvestButton.setEnabled(false);
        }
    }
}
