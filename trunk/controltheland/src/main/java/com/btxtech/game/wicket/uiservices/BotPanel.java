package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.10.2011
 * Time: 16:51:55
 */
public class BotPanel extends Panel {
    @SpringBean
    private BotService botService;

    public BotPanel(String id) {
        super(id);
        add(new TextField<Integer>("botId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbBotConfig dbBotConfig = (DbBotConfig) getDefaultModelObject();
                if (dbBotConfig != null) {
                    return dbBotConfig.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().readDbChild(integer);
                        setDefaultModelObject(dbBotConfig);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, BotPanel.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));


    }
}
