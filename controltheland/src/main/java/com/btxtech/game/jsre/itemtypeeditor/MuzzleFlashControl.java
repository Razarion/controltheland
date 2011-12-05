package com.btxtech.game.jsre.itemtypeeditor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 16:32:21
 */
public class MuzzleFlashControl extends DecoratorPanel {
    private int itemTypeId;
    private ItemTypeSimulation itemTypeSimulation;
    private Logger log = Logger.getLogger(MuzzleFlashControl.class.getName());

    public MuzzleFlashControl(int itemTypeId, ItemTypeSimulation itemTypeSimulation) {
        this.itemTypeId = itemTypeId;
        this.itemTypeSimulation = itemTypeSimulation;
        setupControls();
    }

    private void setupControls() {
        FlexTable flexTable = new FlexTable();
        flexTable.setCellSpacing(6);
        flexTable.setWidget(0, 0, new Button("Fire", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //AttackEffectHandler.getInstance().onAttackForEditor((SyncBaseItem) itemTypeSimulation.getSyncItem());
            }
        }));
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        setWidget(flexTable);
    }
}
