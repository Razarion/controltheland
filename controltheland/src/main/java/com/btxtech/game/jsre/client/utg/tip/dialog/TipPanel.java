package com.btxtech.game.jsre.client.utg.tip.dialog;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.AbstractControlPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 20.11.2011
 * Time: 23:33:00
 */
public class TipPanel extends AbstractControlPanel {
    public static final int WIDTH = 143 + WIDTH_IMAGE_LEFT + WIDTH_IMAGE_RIGHT;
    public static final int HEIGHT = 66 + HEIGHT_IMAGE_TOP + HEIGHT_IMAGE_TOP;

    private String tip;

    public TipPanel(ImageTipEntry tip) {
        super(WIDTH, HEIGHT);
        this.tip = tip.getImage();
        getElement().getStyle().setZIndex(Constants.Z_INDEX_TIP);
        setup();
    }

    @Override
    protected Widget createBody() {
        return ImageHandler.getTipImage(tip);
    }
}
