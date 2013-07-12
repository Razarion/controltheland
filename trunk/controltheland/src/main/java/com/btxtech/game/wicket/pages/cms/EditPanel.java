package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
    private int contentId;
    private int contentCreateEditID = -1;
    private BeanIdPathElement beanIdPathElement;
    private boolean showCreate;
    private boolean showDelete;

    public EditPanel(String id, DbContent dbContent, int contentIdFallback, final BeanIdPathElement beanIdPathElement, final boolean showCreate, final boolean showDelete) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        this.showCreate = showCreate;
        this.showDelete = showDelete;
        contentId = getContentId(dbContent, contentIdFallback);
        if (dbContent instanceof DbContentList && ((DbContentList) dbContent).getDbContentCreateEdit() != null) {
            DbContentList dbContentList = (DbContentList) dbContent;
            contentCreateEditID = dbContentList.getDbContentCreateEdit().getId();
        }
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
            createEditButton = new Button("createEdit", new Model<>(dbContentList.getDbContentCreateEdit().getName())) {
                @Override
                public void onSubmit() {
                    PageParameters parameters = cmsUiService.createPageParametersFromBeanId(beanIdPathElement);
                    parameters.set(CmsPage.CREATE_CONTENT_ID, Integer.toString(contentCreateEditID));
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

    @Override
    public boolean isVisible() {
        return !showDelete && cmsUiService.isEnterEditModeAllowed(contentId, beanIdPathElement)
                || !showDelete && cmsUiService.getEditMode(contentId) != null
                || !showDelete && cmsUiService.isSaveAllowed(contentId, beanIdPathElement)
                || !showDelete && showCreate && cmsUiService.getEditMode(contentId) != null
                || showDelete && cmsUiService.getEditMode(contentId) != null
                || contentCreateEditID != -1 && cmsUiService.isCreateEditAllowed(contentCreateEditID);
    }
}
