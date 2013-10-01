package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.wicket.pages.cms.content.PlainTextArea;
import com.btxtech.game.wicket.pages.cms.content.PlainTextField;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.WysiwygEditor;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.06.2011
 * Time: 10:53:23
 */
public class WritePanel extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private CmsService cmsService;

    public WritePanel(String id, Object value, final BeanIdPathElement beanIdPathElement, DbExpressionProperty dbExpressionProperty) {
        super(id);
        final int contentId = dbExpressionProperty.getId();
        switch (dbExpressionProperty.getEditorType()) {
            case PLAIN_TEXT_FILED:
                add(new PlainTextField("editor", new LoadableDetachableModel<Object>(value) {

                    @Override
                    public void setObject(Object object) {
                        super.setObject(object);
                        cmsUiService.setDataProviderBean(object, beanIdPathElement, contentId);
                    }

                    @Override
                    protected Object load() {
                        return cmsUiService.getDataProviderBean(beanIdPathElement);
                    }
                }));
                break;
            case PLAIN_TEXT_AREA:
                add(new PlainTextArea("editor", new LoadableDetachableModel<Object>(value) {

                    @Override
                    public void setObject(Object object) {
                        super.setObject(object);
                        cmsUiService.setDataProviderBean(object, beanIdPathElement, contentId);
                    }

                    @Override
                    protected Object load() {
                        return cmsUiService.getDataProviderBean(beanIdPathElement);
                    }
                }));
                break;
            case HTML_AREA:
                add(new WysiwygEditor("editor", new LoadableDetachableModel<String>((String) value) {

                    @Override
                    public void setObject(String object) {
                        super.setObject(object);
                        cmsUiService.setDataProviderBean(object, beanIdPathElement, contentId);
                    }

                    @Override
                    protected String load() {
                        return (String) cmsUiService.getDataProviderBean(beanIdPathElement);
                    }
                }));
                break;
            default:
                throw new IllegalArgumentException("Unsupported EditorType: " + dbExpressionProperty.getEditorType());
        }
    }
}
