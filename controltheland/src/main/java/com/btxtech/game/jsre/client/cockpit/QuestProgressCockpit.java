package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.quest.QuestDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 13.04.12
 * Time: 21:32
 */
public class QuestProgressCockpit extends FlowPanel {
    private static final int RADAR_HINT_BLINK = 500;
    private static final String WIDTH = "20%";
    private HTML questTitle;
    private HTML questProgress;
    private VerticalPanel missionStartPanel;
    private VerticalPanel moveToPlanetPanel;
    private Label dialog;
    private Timer radarTimer;
    private QuestInfo activeQuest;
    private boolean moveToNextPlane;

    public QuestProgressCockpit() {
        getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.5)");
        getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
        getElement().getStyle().setPaddingLeft(10, Style.Unit.PX);
        getElement().getStyle().setPaddingRight(10, Style.Unit.PX);
        getElement().getStyle().setProperty("maxWidth", WIDTH);

        setupTitle();
        setupQuestProgress();
        setupMissionStart();
        setupMoveToPlanet();
        setupControlPanel();

        preventEvents(this);
        ClientUserTracker.getInstance().onDialogAppears(this, "QuestProgressCockpit");
    }

    private void setupTitle() {
        questTitle = new HTML();
        questTitle.getElement().getStyle().setColor("#FFFFAA");
        questTitle.getElement().getStyle().setFontSize(18, Style.Unit.PX);
        questTitle.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        questTitle.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        questTitle.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        add(questTitle);
    }

    private void setupQuestProgress() {
        questProgress = new HTML();
        questProgress.getElement().getStyle().setColor("#C7C4BB");
        questProgress.getElement().getStyle().setFontSize(12, Style.Unit.PX);
        questProgress.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        add(questProgress);
    }

    private void setupMissionStart() {
        missionStartPanel = new VerticalPanel();
        add(missionStartPanel);
        HTML text = new HTML("Press the button when ready");
        text.getElement().getStyle().setColor("#C7C4BB");
        text.getElement().getStyle().setFontSize(12, Style.Unit.PX);
        text.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        missionStartPanel.add(text);
        missionStartPanel.setCellHorizontalAlignment(text, HasHorizontalAlignment.ALIGN_LEFT);
        ExtendedCustomButton start = new ExtendedCustomButton("startmission", false, ToolTips.TOOL_TIP_START_MISSION, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ClientLevelHandler.getInstance().startMission();
            }
        });
        missionStartPanel.add(start);
        missionStartPanel.setCellHorizontalAlignment(start, HasHorizontalAlignment.ALIGN_CENTER);
        missionStartPanel.setVisible(false);
    }

    private void setupMoveToPlanet() {
        moveToPlanetPanel = new VerticalPanel();
        add(moveToPlanetPanel);
        HTML text = new HTML("Proceed to the next planet");
        text.getElement().getStyle().setColor("#C7C4BB");
        text.getElement().getStyle().setFontSize(12, Style.Unit.PX);
        text.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        moveToPlanetPanel.add(text);
        moveToPlanetPanel.setCellHorizontalAlignment(text, HasHorizontalAlignment.ALIGN_LEFT);
        ExtendedCustomButton start = new ExtendedCustomButton("startmission", false, ToolTips.TOOL_TIP_NEXT_PLANET, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ClientLevelHandler.getInstance().moveToNextPlanet();
            }
        });
        moveToPlanetPanel.add(start);
        moveToPlanetPanel.setCellHorizontalAlignment(start, HasHorizontalAlignment.ALIGN_CENTER);
        moveToPlanetPanel.setVisible(false);
    }

    private void setupControlPanel() {
        dialog = new Label("Open quest dialog");
        dialog.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        dialog.setTitle(ToolTips.TOOL_TIP_OPEN_QUEST_DIALOG);
        dialog.getElement().getStyle().setColor("#FF0000");
        dialog.getElement().getStyle().setProperty("textShadow", "1px 1px 0px #777777");
        dialog.getElement().getStyle().setFontSize(15, Style.Unit.PX);
        dialog.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        dialog.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        dialog.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        dialog.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                DialogManager.showDialog(new QuestDialog(), DialogManager.Type.QUEUE_ABLE);
            }
        });
        add(dialog);
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 0);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT);
        getElement().getStyle().clearBottom();
        getElement().getStyle().clearLeft();
        getElement().getStyle().setTop(0, Style.Unit.PX);
        getElement().getStyle().setRight(0, Style.Unit.PX);

    }

    public void setActiveQuest(QuestInfo questInfo, String activeQuestProgress) {
        if(moveToNextPlane) {
            return;
        }
        moveToPlanetPanel.setVisible(false);
        if (questInfo != null) {
            if (questInfo.getType() == QuestInfo.Type.MISSION) {
                stopRadarVisualization();
                missionStartPanel.setVisible(true);
                questProgress.setVisible(false);
            } else {
                missionStartPanel.setVisible(false);
                questProgress.setVisible(true);
                if (!questInfo.equals(activeQuest)) {
                    activeQuest = questInfo;
                    stopRadarVisualization();
                    if (questInfo.getRadarPosition() != null) {
                        startRadarVisualization(questInfo.getRadarPosition());
                    }
                }
            }
            questTitle.setHTML(questInfo.getTitle());
        } else {
            stopRadarVisualization();
        }

        if (activeQuestProgress != null) {
            questProgress.setHTML(activeQuestProgress);
        }
        ClientUserTracker.getInstance().onDialogDisappears(this);
        ClientUserTracker.getInstance().onDialogAppears(this, "QuestPanel");


    }

    private void startRadarVisualization(Index position) {
        stopRadarVisualization();
        radarTimer = new TimerPerfmon(PerfmonEnum.QUEST_PROGRESS_COCKPIT_RADAR_HINT) {

            @Override
            public void runPerfmon() {
                RadarPanel.getInstance().blinkHint();
            }
        };
        RadarPanel.getInstance().showHint(position);
        radarTimer.scheduleRepeating(RADAR_HINT_BLINK);
    }

    private void stopRadarVisualization() {
        if (radarTimer != null) {
            radarTimer.cancel();
            radarTimer = null;
            RadarPanel.getInstance().hideHint();
        }
    }

    public void setNoActiveQuest() {
        if(moveToNextPlane) {
            return;
        }
        stopRadarVisualization();
        if (Connection.getInstance().getGameInfo() == null) {
            // Editor
            return;
        }
        questTitle.setHTML("No active quest");
        missionStartPanel.setVisible(false);
        questProgress.setVisible(false);
        moveToPlanetPanel.setVisible(false);
        ClientUserTracker.getInstance().onDialogDisappears(this);
        ClientUserTracker.getInstance().onDialogAppears(this, "No active quest");
        activeQuest = null;
    }


    public void setWrongPlanet(boolean move) {
        if(move) {
            setNoActiveQuest();
        }
        moveToNextPlane = move;
        moveToPlanetPanel.setVisible(move);
    }


    private void preventEvents(Widget widget) {
        widget.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        widget.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                GwtCommon.preventDefault(event);
                ChatCockpit.getInstance().blurFocus();
            }
        }, MouseUpEvent.getType());

        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                GwtCommon.preventDefault(event);
                ChatCockpit.getInstance().blurFocus();
            }
        }, MouseDownEvent.getType());
    }

    public Rectangle getArea() {
        return new Rectangle(getAbsoluteLeft(), getAbsoluteTop(), getOffsetWidth(), getOffsetHeight());
    }

    public void enableQuestControl(boolean enabled) {
        dialog.setVisible(enabled);
    }
}
