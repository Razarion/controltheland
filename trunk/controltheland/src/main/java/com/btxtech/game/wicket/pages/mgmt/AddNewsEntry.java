package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.wicket.uiservices.WysiwygEditor;
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
public class AddNewsEntry extends MgmtWebPage {
    @SpringBean
    private ContentService contentService;

    public AddNewsEntry() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("form");
        add(form);

        final Model<String> title = new Model<>();
        final Model<String> content = new Model<>();
        form.add(new TextField<>("title", title));
        form.add(new WysiwygEditor("content", content));


        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                contentService.createNewsEntryAndSendUserAttentionPacket(title.getObject(), content.getObject());
                setResponsePage(MgmtPage.class);
            }
        });
    }
}
