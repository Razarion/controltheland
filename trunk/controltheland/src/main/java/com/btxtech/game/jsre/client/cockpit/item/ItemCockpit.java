package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.cockpit.AbstractControlPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 08.11.2011
 * Time: 00:29:45
 */
public class ItemCockpit extends AbstractControlPanel implements BuildupItemPanel.BuildListener {
    private static final ItemCockpit INSTANCE = new ItemCockpit();
    private static final int WIDTH = 410;
    private static final int HEIGHT = 166;
    private boolean isActive = false;
    private BuildupItemPanel buildupItemPanel;
    private AbsolutePanel panel;
    private FocusPanel excFocusPanel;

    public static ItemCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemCockpit() {
        super(WIDTH, HEIGHT);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_ITEM_COCKPIT);
        preventEvents();
    }

    public void activate(ClientSyncItem clientSyncItem) {
        excFocusPanel.setFocus(true);

        Index relPosition = TerrainView.getInstance().toRelativeIndex(clientSyncItem.getSyncItem().getSyncItemArea().getPosition());
        relPosition = relPosition.sub(0, clientSyncItem.getSyncItem().getSyncItemArea().getBoundingBox().getHeight() / 2);
        relPosition = relPosition.sub(WIDTH / 2, HEIGHT / 2);
        if (relPosition.getX() < 0) {
            relPosition.setX(0);
        }
        if (relPosition.getY() < 0) {
            relPosition.setY(0);
        }
        if (relPosition.getX() + WIDTH > TerrainView.getInstance().getViewWidth()) {
            relPosition.setX(TerrainView.getInstance().getViewWidth() - WIDTH);
        }
        if (relPosition.getY() + HEIGHT > TerrainView.getInstance().getViewHeight()) {
            relPosition.setY(TerrainView.getInstance().getViewHeight() - HEIGHT);
        }

        panel.setWidgetPosition(this, relPosition.getX(), relPosition.getY());
        setVisible(true);

        buildupItemPanel.display(clientSyncItem);

        isActive = true;
    }

    public void deActivate() {
        if (isActive) {
            isActive = false;
            setVisible(false);
        }
    }

    public void addToParent(AbsolutePanel panel) {
        this.panel = panel;
        panel.add(this, 0, 0);
        setVisible(false);
    }

    // @Override

    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.getElement().getStyle().setColor("#C2D7EC");
        verticalPanel.add(new HTML("Build units:"));
        buildupItemPanel = new BuildupItemPanel(this);
        verticalPanel.add(buildupItemPanel);
        addEscKeyHandler(verticalPanel);
        return verticalPanel;
    }

    private void preventEvents() {
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.stopPropagation();
            }
        }, MouseUpEvent.getType());

        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        }, MouseDownEvent.getType());
        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        }, MouseDownEvent.getType());
    }

    private void addEscKeyHandler(VerticalPanel verticalPanel) {
        excFocusPanel = new FocusPanel();
        excFocusPanel.setPixelSize(1, 1);
        excFocusPanel.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);

        excFocusPanel.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                if (keyDownEvent.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    deActivate();
                }
            }
        });
        excFocusPanel.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (isActive) {
                    excFocusPanel.setFocus(true);
                }
            }
        });
        verticalPanel.add(excFocusPanel);
    }

    @Override
    public void onBuild() {
        deActivate();
    }

    public boolean isActive() {
        return isActive;
    }

    public void onMoneyChanged(double accountBalance) {
        buildupItemPanel.onMoneyChanged(accountBalance);
    }

    public void onStateChanged() {
        buildupItemPanel.onStateChanged();
    }
}
