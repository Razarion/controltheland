package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentBook;
import com.btxtech.game.services.cms.DbContentBooleanExpressionImage;
import com.btxtech.game.services.cms.DbContentRow;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CmsImageSelector;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 25.07.2011
 * Time: 17:51:24
 */
public class ContentBooleanExpressionImageEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentBooleanExpressionImage> ruServiceHelper;

    public ContentBooleanExpressionImageEditor(DbContentBooleanExpressionImage dbContentBooleanExpressionImage) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentBooleanExpressionImage> form = new Form<DbContentBooleanExpressionImage>("form", new CompoundPropertyModel<DbContentBooleanExpressionImage>(new RuModel<DbContentBooleanExpressionImage>(dbContentBooleanExpressionImage, DbContentBooleanExpressionImage.class) {
            @Override
            protected RuServiceHelper<DbContentBooleanExpressionImage> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentAccessPanel("accessPanel", true, true, true, true));
        form.add(new TextField("cssClass"));
        form.add(new TextField("springBeanName"));
        form.add(new TextField("expression"));
        form.add(new CmsImageSelector("trueImage"));
        form.add(new CmsImageSelector("falseImage"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }

}
