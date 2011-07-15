package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentList;
import com.btxtech.game.wicket.pages.cms.content.ContentDetailLink;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 22.06.2011
 * Time: 16:06:48
 */
public class EditPanel extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;

    public EditPanel(String id, DbContent dbContent, int contentIdFallback, final BeanIdPathElement beanIdPathElement, final boolean showCreate, final boolean showDelete) {
        super(id);
        final int contentId = getContentId(dbContent, contentIdFallback);
        Button edit = new Button("edit") {
            @Override
            public void onSubmit() {
                cmsUiService.enterEditMode(contentId, beanIdPathElement);
            }

            @Override
            public boolean isVisible() {
                return !showDelete && cmsUiService.isEnterEditModeAllowed(contentId, beanIdPathElement);
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
        cancelEdit.setDefaultFormProcessing(false);
        add(cancelEdit);

        Button save = new Button("save") {
            @Override
            public void onSubmit() {
                cmsUiService.save(beanIdPathElement);
            }

            @Override
            public boolean isVisible() {
                return !showDelete && cmsUiService.isSaveAllowed(contentId, beanIdPathElement);
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

        Button createEditButton;
        if (dbContent instanceof DbContentList && ((DbContentList) dbContent).getDbContentCreateEdit() != null) {
            DbContentList dbContentList = (DbContentList) dbContent;
            final int contentCreateEditID = dbContentList.getDbContentCreateEdit().getId();
            createEditButton = new Button("createEdit", new Model<String>(dbContentList.getDbContentCreateEdit().getName())) {
                @Override
                public void onSubmit() {
                    PageParameters parameters = new PageParameters();
                    parameters.put(CmsPage.CREATE_CONTENT_ID, Integer.toString(contentCreateEditID));
                    ContentDetailLink.fillBeanIdPathUrlParameters(beanIdPathElement, parameters);
                    setResponsePage(CmsPage.class, parameters);
                }

                @Override
                public boolean isVisible() {
                    return cmsUiService.isCreateEditAllowed(contentCreateEditID);
                }
            };
        } else {
            createEditButton = new Button("createEdit");
            createEditButton.setVisible(false);
        }
        add(createEditButton);
    }

    private int getContentId(DbContent dbContent, int contentIdFallback) {
        int tmpContentId;
        if (dbContent != null) {
            tmpContentId = dbContent.getId();
        } else {
            tmpContentId = contentIdFallback;
        }
        return tmpContentId;
    }

}
