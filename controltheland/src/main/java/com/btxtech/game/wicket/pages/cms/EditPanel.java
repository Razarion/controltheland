package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 22.06.2011
 * Time: 16:06:48
 */
public class EditPanel extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;

    public EditPanel(String id, final int contentId, final BeanIdPathElement beanIdPathElement, final boolean showCreate, final boolean showDelete) {
        super(id);
        Button edit = new Button("edit") {
            @Override
            public void onSubmit() {
                cmsUiService.enterEditMode(contentId);
            }

            @Override
            public boolean isVisible() {
                return !showDelete && cmsUiService.isEnterEditModeAllowed(contentId);
            }
        };
        add(edit);

        Button cancelEdit = new Button("cancelEdit") {
            @Override
            public void onSubmit() {
                cmsUiService.leaveEditMode();
            }

            @Override
            public boolean isVisible() {
                return !showDelete && cmsUiService.getEditMode(contentId) != null;
            }
        };
        add(cancelEdit);

        Button save = new Button("save") {
            @Override
            public void onSubmit() {
                cmsUiService.save(beanIdPathElement);
            }

            @Override
            public boolean isVisible() {
                return !showDelete && cmsUiService.isSaveAllowed(contentId);
            }
        };
        add(save);

        Button create = new Button("create") {
            @Override
            public void onSubmit() {
                cmsUiService.createBean(beanIdPathElement);
            }

            @Override
            public boolean isVisible() {
                return !showDelete && showCreate && cmsUiService.getEditMode(contentId) != null;
            }
        };
        add(create);

        Button delete = new Button("delete") {
            @Override
            public void onSubmit() {
                cmsUiService.deleteBean(beanIdPathElement);
            }

            @Override
            public boolean isVisible() {
                return showDelete && cmsUiService.getEditMode(contentId) != null;
            }
        };
        add(delete);
    }
}
