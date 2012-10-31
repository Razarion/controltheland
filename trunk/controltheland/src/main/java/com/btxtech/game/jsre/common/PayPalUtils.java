package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.Connection;
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

    private static final boolean IS_SANDBOX = false;
    private static final String SANDBOX_ACTION_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String SANDBOX_SUBMIT_IMAGE_URL = "https://www.sandbox.paypal.com/en_US/i/btn/btn_buynowCC_LG.gif";
    private static final String SANDBOX_PIXEL_IMAGE_URL = "https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif";

    public static Widget createBuyNowButton(String hostedButtomId) {
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
        flowPanel.add(new Hidden("custom", Connection.getInstance().getUserName()));

        // flowPanel.add(new Hidden("notify_url", "TODO"));
        if (IS_SANDBOX) {
            fillSandboxParams(form, flowPanel);
        } else {
            fillParams(form, flowPanel, hostedButtomId);
        }

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

        // Add an event handler to the form.
        /*
         * form.addSubmitHandler(new FormPanel.SubmitHandler() { public void
         * onSubmit(SubmitEvent event) { // This event is fired just before the
         * form is submitted. We can take // this opportunity to perform
         * validation. if (tb.getText().length() == 0) {
         * Window.alert("The text box must not be empty"); event.cancel(); } }
         * });
         */
        /*
         * form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
         * public void onSubmitComplete(SubmitCompleteEvent event) { // When the
         * form submission is successfully completed, this event is // fired.
         * Assuming the service returned a response of type text/html, // we can
         * get the result text here (see the FormPanel documentation for //
         * further explanation). Window.alert(event.getResults()); } });
         */
        return form;
    }

    private static void fillParams(FormPanel form, FlowPanel flowPanel, String hostedButtomId) {
        form.setAction(ACTION_URL);
        flowPanel.add(new Hidden("hosted_button_id", hostedButtomId));
    }

    private static void fillSandboxParams(FormPanel form, FlowPanel flowPanel) {
        form.setAction(SANDBOX_ACTION_URL);
        flowPanel
                .add(new Hidden(
                        "encrypted",
                        "-----BEGIN PKCS7-----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-----END PKCS7-----"));
    }

}
