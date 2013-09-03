package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 02.04.13
 * Time: 16:04
 */
public class SendServerRebootMessage extends MgmtWebPage {
    @SpringBean
    private ServerGlobalConnectionService connectionService;

    public SendServerRebootMessage() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("rebootMessageForm");
        add(form);

        final Model<Integer> rebootModel = new Model<>(60);
        final Model<Integer> downTimeModel = new Model<>(5);
        form.add(new TextField<>("rebootInSeconds", rebootModel, Integer.class));
        form.add(new TextField<>("downTimInMinutes", downTimeModel, Integer.class));


        form.add(new Button("send") {
            @Override
            public void onSubmit() {
                if (rebootModel.getObject() == null || downTimeModel.getObject() == null) {
                    error("Enter a valid number for 'Reboot in seconds' and 'Downtime in minutes'");
                }
                connectionService.sendServerRebootMessage(rebootModel.getObject(), downTimeModel.getObject());
            }
        });
    }
}
