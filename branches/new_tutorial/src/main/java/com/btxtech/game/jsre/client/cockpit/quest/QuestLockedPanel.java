package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.unlock.ClientUnlockServiceImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class QuestLockedPanel extends Composite {
    private static QuestLockedPanelUiBinder uiBinder = GWT.create(QuestLockedPanelUiBinder.class);
    @UiField
    Button button;
    private QuestInfo questInfo;

    interface QuestLockedPanelUiBinder extends UiBinder<Widget, QuestLockedPanel> {
    }

    public QuestLockedPanel(QuestInfo questInfo) {
        this.questInfo = questInfo;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        ClientUnlockServiceImpl.getInstance().askUnlockQuest(questInfo, null);
    }
}
