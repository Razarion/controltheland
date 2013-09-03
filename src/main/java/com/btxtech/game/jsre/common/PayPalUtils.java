package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.ClientUserService;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class PayPalUtils {
    private static final String ACTION_URL = "https://www.paypal.com/cgi-bin/webscr";
    private static final String SUBMIT_IMAGE_URL = "https://www.paypalobjects.com/en_US/i/btn/btn_buynowCC_LG.gif";
    private static final String PIXEL_IMAGE_URL = "https://www.paypalobjects.com/en_US/i/scr/pixel.gif";

    public static final boolean IS_SANDBOX = false;
    private static final String SANDBOX_ACTION_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String SANDBOX_SUBMIT_IMAGE_URL = "https://www.sandbox.paypal.com/en_US/i/btn/btn_buynowCC_LG.gif";
    private static final String SANDBOX_PIXEL_IMAGE_URL = "https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif";

    public static Widget createBuyNowButton(PayPalButton payPalButton) {
        final FormPanel form = new FormPanel("_blank");
        form.setMethod(FormPanel.METHOD_POST);

        form.setWidget(new Button("Submit", new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.submit();
            }
        }));

        FlowPanel flowPanel = new FlowPanel();
        form.setWidget(flowPanel);
        flowPanel.add(new Hidden("cmd", "_s-xclick"));
        flowPanel.add(new Hidden("custom", Integer.toString(ClientUserService.getInstance().getSimpleUser().getId())));

        if (IS_SANDBOX) {
            form.setAction(SANDBOX_ACTION_URL);
        } else {
            form.setAction(ACTION_URL);
        }
        flowPanel.add(new Hidden("hosted_button_id", payPalButton.getHostedButtonId()));

        Image pixelIMag = new Image(IS_SANDBOX ? SANDBOX_PIXEL_IMAGE_URL : PIXEL_IMAGE_URL);
        pixelIMag.setPixelSize(1, 1);
        pixelIMag.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        flowPanel.add(pixelIMag);

        Image submitImage = new Image(IS_SANDBOX ? SANDBOX_SUBMIT_IMAGE_URL : SUBMIT_IMAGE_URL);
        submitImage.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        submitImage.setAltText("PayPal - The safer, easier way to pay online!");
        flowPanel.add(new PushButton(submitImage, new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.submit();
            }

        }));
        return form;
    }

}
