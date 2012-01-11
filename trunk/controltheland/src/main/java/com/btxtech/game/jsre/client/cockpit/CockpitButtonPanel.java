package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.WebBrowserCustomButton;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 07.11.2011
 * Time: 18:15:38
 */
public class CockpitButtonPanel extends AbstractControlPanel {
    private ExtendedCustomButton sellButton;

    public CockpitButtonPanel(int width) {
        super(width);
        setup();
    }

    @Override
    protected Widget createBody() {
        Grid grid = new Grid(2, 3);

        //Scroll home
        ExtendedCustomButton scrollHome = new ExtendedCustomButton("scrollHomeButton", false, ToolTips.TOOL_TIP_SCROLL_HOME, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TerrainView.getInstance().moveToHome();
            }
        });
        grid.setWidget(0, 0, scrollHome);
        // Sell button
        sellButton = new ExtendedCustomButton("sellButton", true, ToolTips.TOOL_TIP_SELL, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExtendedCustomButton btn = (ExtendedCustomButton) event.getSource();
                SelectionHandler.getInstance().setSellMode(btn.isDown());
            }
        });
        grid.setWidget(0, 1, sellButton);
        // Sell button
        ExtendedCustomButton mute = new ExtendedCustomButton("speakerButton", true, ToolTips.TOOL_TIP_SELL, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExtendedCustomButton btn = (ExtendedCustomButton) event.getSource();
                SoundHandler.getInstance().mute(btn.isDown());
            }
        });
        grid.setWidget(0, 2, mute);
        grid.setWidget(1, 0, new WebBrowserCustomButton("terminalButton", ToolTips.TOOL_TIP_TERMINAL, CmsUtil.CmsPredefinedPage.USER_PAGE));
        grid.setWidget(1, 1, new WebBrowserCustomButton("highscoreButton", ToolTips.TOOL_TIP_HIGH_SCORE, CmsUtil.CmsPredefinedPage.HIGH_SCORE));
        return grid;
    }

    public void clearSellMode() {
        sellButton.setDownState(false);
    }


}
