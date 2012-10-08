package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 13.08.2012
 * Time: 18:38:58
 */
public class SoundLibrary extends MgmtWebPage {
    @SpringBean
    private SoundService soundService;

    public SoundLibrary() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbSound>("sounds", "saveSounds", "createSound", false, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbSound> dbSoundItem) {
                displayId(dbSoundItem);
                super.extendedPopulateItem(dbSoundItem);
                Label mp3Source = new Label("mp3Source", "");
                mp3Source.add(new SimpleAttributeModifier("src", SoundHandler.buildUrl(dbSoundItem.getModelObject().getId(), Constants.SOUND_CODEC_TYPE_MP3)));
                dbSoundItem.add(mp3Source);
                dbSoundItem.add(new FileUploadField("mp3File", new IModel<FileUpload>() {
                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        dbSoundItem.getModelObject().setDataMp3(fileUpload.getBytes());
                    }

                    @Override
                    public void detach() {
                        //Ignored
                    }
                }));
                Label oggSource = new Label("oggSource", "");
                oggSource.add(new SimpleAttributeModifier("src", SoundHandler.buildUrl(dbSoundItem.getModelObject().getId(), Constants.SOUND_CODEC_TYPE_OGG)));
                dbSoundItem.add(oggSource);
                dbSoundItem.add(new FileUploadField("oggFile", new IModel<FileUpload>() {
                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        dbSoundItem.getModelObject().setDataOgg(fileUpload.getBytes());
                    }

                    @Override
                    public void detach() {
                        //Ignored
                    }
                }));
            }

            @Override
            protected CrudRootServiceHelper<DbSound> getCrudRootServiceHelperImpl() {
                return soundService.getSoundLibraryCrud();
            }
        };
    }
}