package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ImageHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 07.11.2011
 * Time: 18:15:38
 */
public class CockpitControlPanel extends AbstractControlPanel {
    public CockpitControlPanel(int width, int height) {
        super(width, height);
    }

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setHeight("100%");
        HTML mission = new HTML("<B>Mission</B> adasd asd asdasd  asdas  asdas rw gtz hbe th t zqw etf  uilo8iktz  ew re t rt4");
        verticalPanel.add(mission);        
        Grid grid = new Grid(3, 2);
        verticalPanel.add(grid);
        grid.setWidget(0, 0, mission);
        grid.getCellFormatter().getElement(0, 0).setAttribute("colspan", "2");
        grid.getElement().getStyle().setColor("#C2D7EC");
        grid.setWidget(1, 0, ImageHandler.getIcon16("box"));
        grid.setText(1, 1, "Noob 1");
        grid.setWidget(2, 0, ImageHandler.getIcon16("cross"));
        grid.setText(2, 1, "100");
        return verticalPanel;
    }
}
