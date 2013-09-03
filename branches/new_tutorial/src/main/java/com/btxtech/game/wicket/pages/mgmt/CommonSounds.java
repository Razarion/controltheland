package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.media.DbCommonSound;
import com.btxtech.game.services.media.SoundService;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.SoundPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 15.08.12
 * Time: 14:11
 */
public class CommonSounds extends MgmtWebPage {
    @SpringBean
    private SoundService soundService;

    public CommonSounds() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbCommonSound>("sounds", "saveSounds", "createSound", false, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbCommonSound> commonSoundItem) {
                displayId(commonSoundItem);
                commonSoundItem.add(new DropDownChoice<>("type", Arrays.asList(DbCommonSound.Type.values())));
                commonSoundItem.add(new SoundPanel("dbSound"));
            }

            @Override
            protected CrudRootServiceHelper<DbCommonSound> getCrudRootServiceHelperImpl() {
                return soundService.getCommonSoundCrud();
            }
        };
    }

}
