package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 13.04.12
 * Time: 21:32
 */
public class QuestProgressCockpit extends VerticalPanel {
    private HTML questTitle;
    private HTML questProgress;

    public QuestProgressCockpit() {
        getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.5)");
        getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
        getElement().getStyle().setPaddingLeft(10, Style.Unit.PX);
        getElement().getStyle().setPaddingRight(10, Style.Unit.PX);
        getElement().getStyle().setProperty("maxWidth", "20%");

        questTitle = new HTML();
        questTitle.getElement().getStyle().setColor("#FFFFAA");
        questTitle.getElement().getStyle().setOpacity(1.0);
        add(questTitle);

        questProgress = new HTML();
        questProgress.getElement().getStyle().setColor("#C7C4BB");
        questProgress.getElement().getStyle().setOpacity(1.0);
        add(questProgress);

        preventEvents(this);
        ClientUserTracker.getInstance().onDialogAppears(this, "QuestProgressCockpit");
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 0);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);
        getElement().getStyle().clearBottom();
        getElement().getStyle().clearLeft();
        getElement().getStyle().setTop(0, Style.Unit.PX);
        getElement().getStyle().setRight(0, Style.Unit.PX);

    }

    public void setActiveQuest(String activeQuestTitle, String activeQuestProgress, Integer activeQuestLevelTaskId) {
        if (activeQuestTitle != null && activeQuestLevelTaskId != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("<H2>");
            builder.append(CmsUtil.getUrl4LevelPage(activeQuestLevelTaskId, activeQuestTitle));
            builder.append("</H2>");
            questTitle.setHTML(builder.toString());
        }
        if (activeQuestProgress != null) {
            questProgress.setHTML(activeQuestProgress);
        }
        ClientUserTracker.getInstance().onDialogDisappears(this);
        ClientUserTracker.getInstance().onDialogAppears(this, activeQuestTitle);
    }

    public void setNoActiveQuest() {
        if (Connection.getInstance().getGameInfo() == null) {
            // Editor
            return;
        }
        questTitle.setHTML("<H2>No active quest</H2>");
        StringBuilder builder = new StringBuilder();
        builder.append("<a href='");
        builder.append(Connection.getInstance().getGameInfo().getPredefinedUrls().get(CmsUtil.CmsPredefinedPage.USER_PAGE));
        builder.append("' target='_blank' style='color: #C7C4BB; text-decoration: none;'>Click here to activate a quest or mission</a>");
        questProgress.setHTML(builder.toString());
        ClientUserTracker.getInstance().onDialogDisappears(this);
        ClientUserTracker.getInstance().onDialogAppears(this, "No active quest");
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
                ItemCockpit.getInstance().deActivate();
                GwtCommon.preventDefault(event);
                ChatCockpit.getInstance().blurFocus();
            }
        }, MouseDownEvent.getType());
    }
}
