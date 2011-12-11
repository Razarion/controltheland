package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 16:32:21
 */
public class MuzzleFlashControl extends DecoratorPanel {
    private static final int CROSS_SIDE_LENGTH = 5;
    private Logger log = Logger.getLogger(MuzzleFlashControl.class.getName());
    private SyncBaseItem target;
    boolean showInEditor = false;
    private RotationControl rotationControl;
    private IntegerBox integerBoxX;
    private IntegerBox integerBoxY;
    private int imageNr;
    private WeaponType weaponType;
    private SyncBaseItem syncBaseItem;
    private int muzzleFlashEdit = 0;

    private void setupControls() {
        FlexTable flexTable = new FlexTable();
        flexTable.setCellSpacing(6);
        CheckBox showSimulation = new CheckBox("Show in Simulation");
        showSimulation.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                if (booleanValueChangeEvent.getValue()) {
                    if (target == null || !target.isAlive()) {
                        try {
                            target = (SyncBaseItem) ItemContainer.getInstance().createSimulationSyncObject(new ItemTypeAndPosition(ItemTypeEditorPanel.ENEMY_BASE, 1, 1, new Index(250, 150), 0));
                            target.setHealth(1000000);
                        } catch (NoSuchItemTypeException e) {
                            log.log(Level.SEVERE, "", e);
                        }
                    }
                    ClientSyncItem clientSyncItem = CommonJava.getFirst(ItemContainer.getInstance().getOwnItems());
                    clientSyncItem.getSyncBaseItem().setHealth(1000000);
                    ActionHandler.getInstance().attack(clientSyncItem.getSyncBaseItem(),
                            target,
                            clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(),
                            0,
                            false);
                } else {
                    if (target != null && target.isAlive()) {
                        ItemContainer.getInstance().killSyncItem(target, null, true, false);
                    }
                }
            }
        });
        flexTable.setWidget(0, 0, showSimulation);
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        CheckBox showEditor = new CheckBox("Show in Editor");
        showEditor.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                showInEditor = booleanValueChangeEvent.getValue();
                rotationControl.update();
            }
        });
        flexTable.setWidget(1, 0, showEditor);
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        flexTable.setText(2, 0, "Muzzle Flash Count");
        flexTable.setWidget(2, 1, createMuzzleFlashCount());
        flexTable.setText(3, 0, "Edit Muzzle Flash");
        flexTable.setWidget(3, 1, createEditMuzzleFlash());
        flexTable.setText(4, 0, "Muzzle X");
        flexTable.setWidget(4, 1, createPositionBoxX());
        flexTable.setText(5, 0, "Muzzle Y");
        flexTable.setWidget(5, 1, createPositionBoxY());
        setWidget(flexTable);
    }

    private Widget createMuzzleFlashCount() {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        final IntegerBox muzzleFlashCount = new IntegerBox();
        muzzleFlashCount.setValue(weaponType.getMuzzleFlashCount());
        muzzleFlashCount.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> integerValueChangeEvent) {
                if (integerValueChangeEvent.getValue() < 2) {
                    return;
                }
                weaponType.changeMuzzleFlashCount(integerValueChangeEvent.getValue());
            }
        });
        horizontalPanel.add(muzzleFlashCount);
        horizontalPanel.add(new Button("+", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                rotationControl.update();
                muzzleFlashCount.setValue(muzzleFlashCount.getValue() + 1, true);
            }
        }));
        horizontalPanel.add(new Button("-", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (muzzleFlashCount.getValue() < 2) {
                    return;
                }
                rotationControl.update();
                muzzleFlashCount.setValue(muzzleFlashCount.getValue() - 1, true);
            }
        }));
        return horizontalPanel;
    }

    private Widget createEditMuzzleFlash() {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        final Label muzzleFlashEditLabel = new Label(Integer.toString(muzzleFlashEdit + 1));
        horizontalPanel.add(muzzleFlashEditLabel);
        horizontalPanel.add(new Button("+", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (muzzleFlashEdit < weaponType.getMuzzleFlashCount() - 1) {
                    muzzleFlashEdit++;
                    muzzleFlashEditLabel.setText(Integer.toString(muzzleFlashEdit + 1));
                    rotationControl.update();
                }
            }
        }));
        horizontalPanel.add(new Button("-", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (muzzleFlashEdit > 0) {
                    muzzleFlashEdit--;
                    muzzleFlashEditLabel.setText(Integer.toString(muzzleFlashEdit + 1));
                    rotationControl.update();
                }
            }
        }));
        return horizontalPanel;
    }

    public void onImageChanged(int currentImage) {
        imageNr = currentImage;
        if (weaponType == null) {
            return;
        }
        Index index = weaponType.getMuzzleFlashPosition(muzzleFlashEdit, imageNr);
        integerBoxX.setValue(index.getX());
        integerBoxY.setValue(index.getY());

        if (target == null || !target.isAlive()) {
            return;
        }
        double angel = syncBaseItem.getSyncItemArea().getBoundingBox().imageNumberToAngel(currentImage);
        Index targetPos = syncBaseItem.getSyncItemArea().getPosition().getPointFromAngelToNord(angel, weaponType.getRange() * 0.9);
        target.getSyncItemArea().setPosition(targetPos);
    }

    public void draw(int imageNr, Context2d context2d) {
        if (!showInEditor) {
            return;
        }
        if (weaponType == null) {
            return;
        }
        Index index = weaponType.getMuzzleFlashPosition(muzzleFlashEdit, imageNr);
        // Draw cross
        Index crossMiddle = ItemTypeView.ITEM_POSITION.add(index);
        context2d.setStrokeStyle("#FF0000");
        context2d.setLineWidth(1.0);
        context2d.beginPath();
        context2d.moveTo(crossMiddle.getX() - CROSS_SIDE_LENGTH, crossMiddle.getY());
        context2d.lineTo(crossMiddle.getX() + CROSS_SIDE_LENGTH, crossMiddle.getY());
        context2d.moveTo(crossMiddle.getX(), crossMiddle.getY() - CROSS_SIDE_LENGTH);
        context2d.lineTo(crossMiddle.getX(), crossMiddle.getY() + CROSS_SIDE_LENGTH);
        context2d.stroke();
    }

    public void onClick(MouseDownEvent event) {
        if (!showInEditor) {
            return;
        }
        weaponType.setMuzzleFlashPosition(muzzleFlashEdit, imageNr, new Index(event.getX(), event.getY()).sub(ItemTypeView.ITEM_POSITION));
        rotationControl.update();
    }

    public void setRotationControl(RotationControl rotationControl) {
        this.rotationControl = rotationControl;
    }

    private Widget createPositionBoxX() {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        integerBoxX = new IntegerBox();
        integerBoxX.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> integerValueChangeEvent) {
                Index index = weaponType.getMuzzleFlashPosition(muzzleFlashEdit, imageNr);
                index.setX(integerValueChangeEvent.getValue());
                weaponType.setMuzzleFlashPosition(muzzleFlashEdit, imageNr, index);
            }
        });
        horizontalPanel.add(integerBoxX);
        horizontalPanel.add(new Button("+", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                rotationControl.update();
                integerBoxX.setValue(integerBoxX.getValue() + 1, true);
            }
        }));
        horizontalPanel.add(new Button("-", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                integerBoxX.setValue(integerBoxX.getValue() - 1, true);
                rotationControl.update();
            }
        }));
        return horizontalPanel;
    }

    private Widget createPositionBoxY() {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        integerBoxY = new IntegerBox();
        integerBoxY.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> integerValueChangeEvent) {
                Index index = weaponType.getMuzzleFlashPosition(muzzleFlashEdit, imageNr);
                index.setY(integerValueChangeEvent.getValue());
                weaponType.setMuzzleFlashPosition(muzzleFlashEdit, imageNr, index);
            }
        });
        horizontalPanel.add(integerBoxY);
        horizontalPanel.add(new Button("+", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                integerBoxY.setValue(integerBoxY.getValue() + 1, true);
                rotationControl.update();
            }
        }));
        horizontalPanel.add(new Button("-", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                integerBoxY.setValue(integerBoxY.getValue() - 1, true);
                rotationControl.update();
            }
        }));
        return horizontalPanel;
    }

    public void init(int imageNr, BaseItemType baseItemType) {
        if (baseItemType.getWeaponType() == null) {
            return;
        }
        this.imageNr = imageNr;
        weaponType = baseItemType.getWeaponType();

        setupControls();

        Index index = weaponType.getMuzzleFlashPosition(muzzleFlashEdit, imageNr);
        integerBoxX.setValue(index.getX());
        integerBoxY.setValue(index.getY());
    }

    public void initSyncItem(SyncBaseItem syncBaseItem) {
        this.syncBaseItem = syncBaseItem;
    }

}
