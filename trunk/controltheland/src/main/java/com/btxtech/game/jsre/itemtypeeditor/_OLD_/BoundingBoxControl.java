package com.btxtech.game.jsre.itemtypeeditor._OLD_;

import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BuildupStep;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccess;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccessAsync;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 16:32:21
 */
public class BoundingBoxControl extends DecoratorPanel {
    private RotationControl rotationControl;
    private int itemTypeId;
    private BoundingBox boundingBox;
    private MuzzleFlashControl muzzleFlashControl;
    private boolean showBoundingBox = true;
    private Logger log = Logger.getLogger(BoundingBoxControl.class.getName());
    private BuildupStepEditorPanel buildupStepEditorPanel;

    public BoundingBoxControl(int itemTypeId, BoundingBox boundingBox, MuzzleFlashControl muzzleFlashControl) {
        this.itemTypeId = itemTypeId;
        this.boundingBox = boundingBox;
        this.muzzleFlashControl = muzzleFlashControl;
        setupControls();
    }

    private void setupControls() {
        FlexTable flexTable = new FlexTable();
        flexTable.setCellSpacing(6);

        CheckBox showBoundingBoxWidget = new CheckBox("Show Bounding Box");
        flexTable.setWidget(0, 0, showBoundingBoxWidget);
        showBoundingBoxWidget.setValue(showBoundingBox);
        showBoundingBoxWidget.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> value) {
                showBoundingBox = value.getValue();
                rotationControl.update();
            }
        });
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        flexTable.setText(1, 0, "Image Width");
        // TODO flexTable.setText(1, 1, Integer.toString(boundingBox.getImageWidth()));
        flexTable.setText(2, 0, "Image Height");
        // TODO flexTable.setText(2, 1, Integer.toString(boundingBox.getImageHeight()));
        flexTable.setText(3, 0, "Width");
        flexTable.setWidget(3, 1, createIntegerBox(boundingBox.getWidth(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                boundingBox.setWidth(value.getValue());
                rotationControl.update();
            }
        }));
        flexTable.setText(4, 0, "Height");
        flexTable.setWidget(4, 1, createIntegerBox(boundingBox.getHeight(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                boundingBox.setHeight(value.getValue());
                rotationControl.update();
            }
        }));
 /*       flexTable.setWidget(5, 0, new Button("Save", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Button button = (Button) event.getSource();
                ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
                List<BuildupStep> buildupSteps = null;
                if (buildupStepEditorPanel != null) {
                    buildupSteps = buildupStepEditorPanel.getBuildupStepDatas();
                }
                itemTypeAccess.saveItemTypeProperties(itemTypeId, boundingBox, muzzleFlashControl.getWeaponType(), buildupSteps, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        button.setEnabled(true);
                        log.log(Level.SEVERE, "saveBoundingBox call failed", caught);
                        DialogManager.showDialog(new MessageDialog("Failure", "Save failed!"), DialogManager.Type.PROMPTLY);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        button.setEnabled(true);
                    }
                });
                button.setEnabled(false);
            }
        })); */
        flexTable.getFlexCellFormatter().setColSpan(5, 0, 2);
        setWidget(flexTable);
    }

    public void setRotationControl(RotationControl rotationControl) {
        this.rotationControl = rotationControl;
    }

    private Widget createIntegerBox(int value, ValueChangeHandler<Integer> changeHandler) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        final IntegerBox integerBox = new IntegerBox();
        integerBox.setValue(value);
        integerBox.addValueChangeHandler(changeHandler);
        horizontalPanel.add(integerBox);
        horizontalPanel.add(new Button("+", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                integerBox.setValue(integerBox.getValue() + 1, true);
            }
        }));
        horizontalPanel.add(new Button("-", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                integerBox.setValue(integerBox.getValue() - 1, true);
            }
        }));
        return horizontalPanel;
    }

    public void draw(SyncItemArea syncItemArea, Context2d context2d) {
        if (!showBoundingBox) {
            return;
        }
        context2d.save();
        context2d.setStrokeStyle("#FF0000");
        context2d.beginPath();
        context2d.moveTo(syncItemArea.getCorner1().getX(), syncItemArea.getCorner1().getY());
        context2d.lineTo(syncItemArea.getCorner2().getX(), syncItemArea.getCorner2().getY());
        context2d.lineTo(syncItemArea.getCorner3().getX(), syncItemArea.getCorner3().getY());
        context2d.lineTo(syncItemArea.getCorner4().getX(), syncItemArea.getCorner4().getY());
        context2d.closePath();
        context2d.stroke();
        context2d.restore();
    }

    public void setBuilupEditorPanel(BuildupStepEditorPanel buildupStepEditorPanel) {
        this.buildupStepEditorPanel = buildupStepEditorPanel;
    }
}
