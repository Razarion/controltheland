package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.MinimizeButton;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.quest.QuestDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.unlock.ClientUnlockServiceImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestVisualisationCockpit extends Composite {
    private static QuestVisualisationCockpitUiBinder uiBinder = GWT.create(QuestVisualisationCockpitUiBinder.class);
    private static final QuestVisualisationCockpit INSTANCE = new QuestVisualisationCockpit();
    @UiField
    Label titleLabel;
    @UiField
    Button questDialogButton;
    @UiField
    SimplePanel mainPanel;
    @UiField
    CheckBox visualiseCheckBox;
    private QuestVisualisationPanel questVisualisationPanel;
    private MinimizeButton minimizeButton = new MinimizeButton(true);
    private static Logger log = Logger.getLogger(QuestVisualisationCockpit.class.getName());

    interface QuestVisualisationCockpitUiBinder extends UiBinder<Widget, QuestVisualisationCockpit> {
    }

    /**
     * Singleton
     */
    private QuestVisualisationCockpit() {
        initWidget(uiBinder.createAndBindUi(this));
        minimizeButton.addWidgetToHide(this);
        QuestVisualisationModel.getInstance().setListener(this);
        questDialogButton.setStyleName("singleButton");
    }

    public static QuestVisualisationCockpit getInstance() {
        return INSTANCE;
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 0);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT);
        getElement().getStyle().clearBottom();
        getElement().getStyle().clearLeft();
        getElement().getStyle().setTop(0, Style.Unit.PX);
        getElement().getStyle().setRight(0, Style.Unit.PX);
        parent.add(minimizeButton, 0, 0);
        minimizeButton.getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT_MINIMIZE_BUTTON);
        minimizeButton.getElement().getStyle().clearBottom();
        minimizeButton.getElement().getStyle().clearLeft();
        minimizeButton.getElement().getStyle().setTop(0, Style.Unit.PX);
        minimizeButton.getElement().getStyle().setRight(0, Style.Unit.PX);
    }

    @UiHandler("questDialogButton")
    void onQuestDialogButtonClick(ClickEvent event) {
        DialogManager.showDialog(new QuestDialog(), DialogManager.Type.QUEUE_ABLE);
    }

    public void updateType(QuestInfo questInfo) {
        questVisualisationPanel = null;
        if (QuestVisualisationModel.getInstance().isNoQuest()) {
            mainPanel.clear();
            titleLabel.setText(ClientI18nHelper.CONSTANTS.noActiveQuest());
        } else if (ClientUnlockServiceImpl.getInstance().isQuestLocked(questInfo)) {
            mainPanel.setWidget(new QuestLockedPanel(questInfo));
            titleLabel.setText(questInfo.getTitle());
        } else if (QuestVisualisationModel.getInstance().isShowStartMission()) {
            mainPanel.setWidget(new StartMissionPanel());
            titleLabel.setText(questInfo.getTitle());
        } else if (QuestVisualisationModel.getInstance().isNextPlanet()) {
            displayNextPlanetPanel();
        } else {
            questVisualisationPanel = new QuestVisualisationPanel(questInfo);
            mainPanel.setWidget(questVisualisationPanel);
            titleLabel.setText(questInfo.getTitle());
        }
        minimizeButton.maximize();
        if(QuestVisualisationModel.getInstance().isNextPlanet()) {
            visualiseCheckBox.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        } else {
            visualiseCheckBox.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            visualiseCheckBox.setValue(true);
        }
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(true);
        questDialogButton.setVisible(Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE && !QuestVisualisationModel.getInstance().isNextPlanet());
    }

    public void displayNextPlanetPanel() {
        if(!QuestVisualisationModel.getInstance().isNextPlanet()) {
            throw new IllegalArgumentException("QuestVisualisationCockpit.displayNextPlanetPanel() is not next planet");
        }
        if (ClientUnlockServiceImpl.getInstance().isPlanetLocked(QuestVisualisationModel.getInstance().getNextPlanet())) {
            mainPanel.setWidget(new NextPlanetLocketPanel());
        } else {
            mainPanel.setWidget(new NextPlanetPanel());
        }
        titleLabel.setText("");
    }

    public void updateQuestProgress(QuestProgressInfo questProgressInfo) {
        if (QuestVisualisationModel.getInstance().isNextPlanet()) {
            return;
        }
        if (questVisualisationPanel == null) {
            throw new NullPointerException("QuestVisualisationCockpit.updateQuestProgress() questVisualisationPanel == null");
        }
        try {
            questVisualisationPanel.update(questProgressInfo);
        } catch (Exception e) {
            log.log(Level.SEVERE, "QuestVisualisationCockpit.updateQuestProgress()", e);
        }
    }

    @UiHandler("visualiseCheckBox")
    void onVisualiseCheckBoxClick(ClickEvent event) {
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(visualiseCheckBox.getValue());
    }
}
