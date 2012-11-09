package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.client.common.info.CommonClipInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbClip;
import com.btxtech.game.services.media.DbCommonClip;
import com.btxtech.game.services.media.DbCommonSound;
import com.btxtech.game.wicket.uiservices.ClipPanel;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.ImageSpriteMapPanel;
import com.btxtech.game.wicket.uiservices.SoundPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 13.08.2012
 * Time: 18:38:58
 */
public class ClipLibrary extends MgmtWebPage {
    @SpringBean
    private ClipService clipService;

    public ClipLibrary() {
        add(new FeedbackPanel("msgs"));

        Form clipLibraryForm = new Form("clipLibraryForm");
        add(clipLibraryForm);

        new CrudRootTableHelper<DbClip>("clipLibrary", "saveClipLibrary", "createClip", false, clipLibraryForm, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbClip> dbClipItem) {
                displayId(dbClipItem);
                super.extendedPopulateItem(dbClipItem);
                dbClipItem.add(new ImageSpriteMapPanel("dbImageSpriteMap"));
                dbClipItem.add(new SoundPanel("dbSound"));
            }

            @Override
            protected CrudRootServiceHelper<DbClip> getCrudRootServiceHelperImpl() {
                return clipService.getClipLibraryCrud();
            }

        };

        Form commonClipForm = new Form("commonClipForm");
        add(commonClipForm);

        new CrudRootTableHelper<DbCommonClip>("commonClips", "saveCommonClips", "createCommonClip", false, commonClipForm, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbCommonClip> dbCommonClipItem) {
                displayId(dbCommonClipItem);
                dbCommonClipItem.add(new DropDownChoice<>("type", Arrays.asList(CommonClipInfo.Type.values())));
                dbCommonClipItem.add(new ClipPanel("dbClip"));
            }

            @Override
            protected CrudRootServiceHelper<DbCommonClip> getCrudRootServiceHelperImpl() {
                return clipService.getCommonClipCrud();
            }

        };

    }
}