package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * User: beat
 * Date: 07.02.2012
 * Time: 01:14:40
 */
public class LevelPanel extends AbsolutePanel {
    private static final int WEIGHT = 48;
    private static final int HEIGHT = 48;
    private Label levelName;
    private boolean isBlinking = false;
    private Image blinkImage;
    private Image normalImage;

    public LevelPanel() {
        setPixelSize(WEIGHT, HEIGHT);
        setTitle(ToolTips.TOOL_TIP_LEVEL);
        levelName = new Label("?");
        levelName.getElement().getStyle().setProperty("textAlign", "center");
        levelName.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        levelName.getElement().getStyle().setFontSize(20, Style.Unit.PX);
        levelName.getElement().getStyle().setFontSize(20, Style.Unit.PX);
        levelName.getElement().getStyle().setZIndex(2);
        levelName.getElement().getStyle().setColor("Maroon");
        levelName.setWidth("100%");
        add(levelName, 0, 10);
        blinkImage = ImageHandler.getCockpitImage("levelAnimated.gif");
        blinkImage.getElement().getStyle().setZIndex(1);
        add(blinkImage, 0, 0);
        normalImage = ImageHandler.getCockpitImage("level.jpg");
        normalImage.getElement().getStyle().setZIndex(1);
        add(normalImage, 0, 0);
        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                stopBlink();
                Window.open(Connection.getInstance().getGameInfo().getPredefinedUrls().get(CmsUtil.CmsPredefinedPage.USER_PAGE), CmsUtil.TARGET_BLANK, "");
            }
        }, ClickEvent.getType());
        getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }

    public void onLevelUp(LevelScope levelScope) {
        levelName.setText(Integer.toString(levelScope.getNumber()));
        startBlink();
    }

    public void onLevelTaskDone() {
        startBlink();
    }

    private void startBlink() {
        if (isBlinking) {
            return;
        }
        blinkImage.setVisible(true);
        normalImage.setVisible(false);
        isBlinking = true;
    }

    private void stopBlink() {
        if (!isBlinking) {
            return;
        }
        blinkImage.setVisible(false);
        normalImage.setVisible(true);
        isBlinking = false;
    }
}
