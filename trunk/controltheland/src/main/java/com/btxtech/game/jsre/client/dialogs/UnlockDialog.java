package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 11.02.13
 * Time: 01:16
 */
public class UnlockDialog extends Dialog {
    private int razarion;
    private int unlockRazarion;
    private Runnable runnable;
    private String message;

    public UnlockDialog(final BaseItemType baseItemType, int razarion, final Runnable successRunnable) {
        super(ClientI18nHelper.CONSTANTS.unlockItemDialogTitle());
        this.razarion = razarion;
        unlockRazarion = baseItemType.getUnlockRazarion();
        message = ClientI18nHelper.CONSTANTS.itemIsLocked(ClientI18nHelper.getLocalizedString(baseItemType.getI18Name()));
        runnable = new Runnable() {
            @Override
            public void run() {
                Connection.getInstance().unlockItemType(baseItemType.getId(), successRunnable);
            }
        };
    }

    public UnlockDialog(final QuestInfo questInfo, Integer razarion, final Runnable successRunnable) {
        super(ClientI18nHelper.CONSTANTS.unlockQuestDialogTitle());
        this.razarion = razarion;
        unlockRazarion = questInfo.getUnlockRazarion();
        message = ClientI18nHelper.CONSTANTS.questIsLocked(questInfo.getTitle());
        runnable = new Runnable() {
            @Override
            public void run() {
                Connection.getInstance().unlockQuest(questInfo.getId(), successRunnable);
            }
        };
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        VerticalPanel verticalPanel = new VerticalPanel();
        dialogVPanel.add(verticalPanel);
        verticalPanel.add(new Label(message));
        verticalPanel.add(new Label(ClientI18nHelper.CONSTANTS.unlockRazarionCost(unlockRazarion)));
        verticalPanel.add(new Label(ClientI18nHelper.CONSTANTS.availableRazarion(razarion)));
        Button unlockButton = new Button(ClientI18nHelper.CONSTANTS.unlockButton());
        unlockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
                runnable.run();
            }
        });
        unlockButton.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        dialogVPanel.add(unlockButton);
    }
}
