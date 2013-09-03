package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: 01.02.13
 * Time: 16:41
 */
public class SendEmailPage extends MgmtWebPage {
    @SpringBean
    private UserService userService;
    @SpringBean
    private MgmtService mgmtService;

    public SendEmailPage() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        final Model<String> usersModel = new Model<>();
        final ModalWindow modalWindow = new ModalWindow("modalWindow");
        modalWindow.setTitle("Check users");
        modalWindow.setContent(new CheckUserEmailPanel(modalWindow.getContentId()) {
            @Override
            public String getUsers() {
                return usersModel.getObject();
            }
        });
        form.add(modalWindow);
        final TextArea textArea = new TextArea<>("users", usersModel);
        form.add(textArea);
        form.add(new AjaxButton("checkUserButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalWindow.show(target);
            }
        });

        final Model<String> subjectFieldModel = new Model<>();
        form.add(new TextField<>("subjectField", subjectFieldModel));

        final Model<String> bodyFieldModel = new Model<>();
        form.add(new TextArea<>("bodyField", bodyFieldModel));

        form.add(new Button("sendButton") {
            @Override
            public void onSubmit() {
                List<User> users = userService.getUsersWithEmail(usersModel.getObject());
                if (users.isEmpty()) {
                    info("No email sent. No valid users.");
                    return;
                }

                int sent = 0;
                int failed = 0;
                for (User user : users) {
                    try {
                        mgmtService.sendEmail(user, subjectFieldModel.getObject(), bodyFieldModel.getObject());
                        sent++;
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                        info(e.getMessage());
                        failed++;
                    }
                }
                info("Emails sent: " + sent);
                if (failed > 0) {
                    info("Failed: " + failed);
                }
            }
        });

    }

}

