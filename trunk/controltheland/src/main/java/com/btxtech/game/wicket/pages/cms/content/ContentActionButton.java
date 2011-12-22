package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentActionButton;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 06.07.2011
 * Time: 11:56:27
 */
public class ContentActionButton extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentActionButton(String id, DbContentActionButton dbContentActionButton, final BeanIdPathElement beanIdPathElement) {
        super(id);
        contentId = dbContentActionButton.getId();

        add(new Button("button", new Model<String>(dbContentActionButton.getName())) {
            @Override
            public void onSubmit() {
                cmsUiService.invokeCall(contentId, beanIdPathElement);
            }

            @Override
            public boolean isVisible() {
                return cmsUiService.isConditionFulfilled(contentId, beanIdPathElement);
            }
        });

        add(new Label("label", dbContentActionButton.getUnfilledHtml()){
            @Override
            public boolean isVisible() {
                return !cmsUiService.isConditionFulfilled(contentId, beanIdPathElement);
            }

        }.setEscapeModelStrings(dbContentActionButton.getUnfilledHtmlEscapeMarkup()));
    }
}
