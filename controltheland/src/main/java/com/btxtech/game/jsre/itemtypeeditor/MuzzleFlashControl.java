package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 16:32:21
 */
public class MuzzleFlashControl extends DecoratorPanel {
    private Logger log = Logger.getLogger(MuzzleFlashControl.class.getName());
    private SyncBaseItem target;

    public MuzzleFlashControl() {
        setupControls();
    }

    private void setupControls() {
        FlexTable flexTable = new FlexTable();
        flexTable.setCellSpacing(6);
        flexTable.setWidget(0, 0, new Button("Fire", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                try {
                    if (target == null || !target.isAlive()) {
                        target = (SyncBaseItem) ItemContainer.getInstance().createSimulationSyncObject(new ItemTypeAndPosition(ItemTypeEditorPanel.ENEMY_BASE, 1, 1, new Index(250, 150), 0));
                        target.setHealth(1000000);
                    }
                } catch (Exception e) {
                    log.log(Level.SEVERE, "", e);
                }

                ClientSyncItem clientSyncItem = CommonJava.getFirst(ItemContainer.getInstance().getOwnItems());
                clientSyncItem.getSyncBaseItem().setHealth(1000000);
                ActionHandler.getInstance().attack(clientSyncItem.getSyncBaseItem(),
                        target,
                        clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(),
                        0,
                        false);
                /* try {
                   ItemContainer.getInstance().getClientSyncItem(target.getId()).setHidden(false);
               } catch (ItemDoesNotExistException e) {
                   log.log(Level.SEVERE, "", e);
               }
               ClientSyncItem clientSyncItem = CommonJava.getFirst(ItemContainer.getInstance().getOwnItems());
               clientSyncItem.getSyncBaseItem().getSyncWeapon().setTarget(target.getId());
               AttackEffectHandler.getInstance().onAttack(clientSyncItem); */
            }
        }));
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        setWidget(flexTable);
    }

    public void onImageChanged(int currentImage) {
        if (target == null || !target.isAlive()) {
            return;
        }
        ClientSyncItem clientSyncItem = CommonJava.getFirst(ItemContainer.getInstance().getOwnItems());
        double angel = clientSyncItem.getSyncBaseItem().getSyncItemArea().getBoundingBox().imageNumberToAngel(currentImage);
        Index targetPos = clientSyncItem.getSyncItem().getSyncItemArea().getPosition().getPointFromAngelToNord(angel, 100);
        target.getSyncItemArea().setPosition(targetPos);

    }
}
