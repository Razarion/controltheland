package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 07.11.2011
 * Time: 18:15:38
 */
public class CockpitControlPanel extends AbstractControlPanel {
    private Label money;
    private HTML mission;
    private Label level;

    public CockpitControlPanel(int width, int height) {
        super(width, height);
        setup();
    }

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setHeight("100%");
        // Mission
        mission = new HTML();
        mission.setTitle(ToolTips.TOOL_TIP_LEVEL_TARGET);
        verticalPanel.add(mission);

        Grid grid = new Grid(3, 2);
        verticalPanel.add(grid);
        grid.setWidget(0, 0, mission);
        grid.getCellFormatter().getElement(0, 0).setAttribute("colspan", "2");
        grid.getElement().getStyle().setColor("#C2D7EC");
        // Level
        Image image = ImageHandler.getIcon16("medal");
        image.setTitle(ToolTips.TOOL_TIP_LEVEL);
        grid.setWidget(1, 0, image);
        level = new Label();
        level.setTitle(ToolTips.TOOL_TIP_LEVEL);
        grid.setWidget(1, 1, level);
        // Money
        image = ImageHandler.getIcon16("money");
        image.setTitle(ToolTips.TOOL_TIP_MONEY);
        grid.setWidget(2, 0, image);
        money = new Label();
        money.setTitle(ToolTips.TOOL_TIP_MONEY);
        grid.setWidget(2, 1, money);
        return verticalPanel;
    }

    public void updateMoney(double accountBalance) {
        money.setText(Integer.toString((int) Math.round(accountBalance)));
    }

    public void setLevel(Level level) {
        this.level.setText(level.getName());
        mission.setHTML(level.getHtml() + " " + CmsUtil.getUrl4LavalPage(level, "More"));
    }
}
