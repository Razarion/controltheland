package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.chat.ChatCockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryDialog;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 25.05.12
 * Time: 17:30
 */
public class InformationCockpit extends VerticalPanel {
    private static final int SHOW_TIME = 7000;
    private AbsolutePanel parent;
    private Logger log = Logger.getLogger(InformationCockpit.class.getName());
    private Timer disappearTimer;
    private boolean isVisible = false;

    public InformationCockpit() {
        getElement().getStyle().setZIndex(Constants.Z_INDEX_INFORMATION_COCKPIT);
        getElement().getStyle().setColor("#FF0000");
        getElement().getStyle().setProperty("textShadow", "1px 1px 0px #0a131c");
        getElement().getStyle().setFontSize(15, Style.Unit.PX);
        getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        preventEvents(this);
    }

    public void setPatent(AbsolutePanel parent) {
        this.parent = parent;
    }

    public void showBoxPicked(String html) {
        clear();
        attachToParent();
        startDisappearTimer();
        add(new HTML(html));
        Label inventoryLink = new Label("Open Inventory");
        inventoryLink.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        inventoryLink.getElement().getStyle().setColor("#FF6666");
        inventoryLink.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                DialogManager.showDialog(new InventoryDialog(), DialogManager.Type.QUEUE_ABLE);
            }
        });
        preventEvents(inventoryLink);
        add(inventoryLink);
    }

    private void startDisappearTimer() {
        if (disappearTimer != null) {
            disappearTimer.cancel();
        }

        disappearTimer = new TimerPerfmon(PerfmonEnum.INFORMATION_COCKPIT) {
            @Override
            public void runPerfmon() {
                removeFromPatent();
            }
        };
        disappearTimer.schedule(SHOW_TIME);
    }

    private void attachToParent() {
        if (parent == null) {
            log.warning("InformationCockpit.attachToParent() parent is null");
            return;
        }
        if (isVisible) {
            return;
        }
        parent.add(this, SideCockpit.INFORMATION_COCKPIT_X, SideCockpit.INFORMATION_COCKPIT_Y);
        isVisible = true;
    }

    private void removeFromPatent() {
        if (parent == null) {
            log.warning("InformationCockpit.removeFromPatent() parent is null");
            return;
        }
        if (!isVisible) {
            return;
        }
        parent.remove(this);
        isVisible = false;
    }

    private void preventEvents(Widget widget) {
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
}
