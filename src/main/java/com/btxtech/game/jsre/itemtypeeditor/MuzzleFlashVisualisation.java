package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;


/**
 * User: beat
 * Date: 03.08.12
 * Time: 18:05
 */
public class MuzzleFlashVisualisation extends AbstractVisualisation {
    private static final int CROSS_SIDE_LENGTH = 5;
    private MuzzleFlashPanel muzzleFlashPanel;

    public MuzzleFlashVisualisation(final MuzzleFlashPanel muzzleFlashPanel) {
        this.muzzleFlashPanel = muzzleFlashPanel;
        getCanvas().addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                Index muzzlePosition = new Index(GwtCommon.correctInt(event.getX()), GwtCommon.correctInt(event.getY()));
                muzzlePosition = muzzlePosition.sub(MIDDLE);
                ItemTypeEditorModel.getInstance().getWeaponType().setMuzzleFlashPosition(muzzleFlashPanel.getMuzzleFlashNumber(),
                        ItemTypeEditorModel.getInstance().getCurrentAngelIndex(),
                        muzzlePosition);
                ItemTypeEditorModel.getInstance().fireUpdate();
            }
        });
    }

    @Override
    protected void drawVisualisation(Context2d context2d, SyncItemArea syncItemArea) {
        if (ItemTypeEditorModel.getInstance().getWeaponType() == null) {
            return;
        }
        Index index = ItemTypeEditorModel.getInstance().getWeaponType().getMuzzleFlashPosition(muzzleFlashPanel.getMuzzleFlashNumber(), ItemTypeEditorModel.getInstance().getCurrentAngelIndex());
        Index crossMiddle = MIDDLE.add(index);
        context2d.setStrokeStyle("#FF0000");
        context2d.setLineWidth(1.0);
        context2d.beginPath();
        context2d.moveTo(crossMiddle.getX() - CROSS_SIDE_LENGTH, crossMiddle.getY());
        context2d.lineTo(crossMiddle.getX() + CROSS_SIDE_LENGTH, crossMiddle.getY());
        context2d.moveTo(crossMiddle.getX(), crossMiddle.getY() - CROSS_SIDE_LENGTH);
        context2d.lineTo(crossMiddle.getX(), crossMiddle.getY() + CROSS_SIDE_LENGTH);
        context2d.stroke();
    }
}
