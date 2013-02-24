package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.mgmt.items.ItemsUtil;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 14.03.2011
 * Time: 20:33:57
 */
public class QuestCollectionPanel extends Panel {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public QuestCollectionPanel(String id) {
        this(id, null);
    }

    public QuestCollectionPanel(String id, IModel<Collection<DbLevelTask>> model) {
        super(id, model);
        add(new TextField<>("questId", new IModel<String>() {

            @Override
            public String getObject() {
                Collection<DbLevelTask> dbLevelTasks = (Collection<DbLevelTask>) getDefaultModelObject();
                if (dbLevelTasks == null) {
                    return "";
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (DbLevelTask dbLevelTask : dbLevelTasks) {
                    stringBuilder.append(dbLevelTask.getId());
                    stringBuilder.append(ItemsUtil.DELIMITER);
                }
                return stringBuilder.toString();
            }

            @Override
            public void setObject(String questIdString) {
                Collection<DbLevelTask> dbLevelTasks = (Collection<DbLevelTask>) getDefaultModelObject();
                dbLevelTasks.clear();
                if (questIdString != null) {
                    StringTokenizer st = new StringTokenizer(questIdString, ItemsUtil.DELIMITER);
                    while (st.hasMoreTokens()) {
                        int questId = Integer.parseInt(st.nextToken());
                        dbLevelTasks.add(userGuidanceService.getDbLevelTask4Id(questId));
                    }
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
    }
}
