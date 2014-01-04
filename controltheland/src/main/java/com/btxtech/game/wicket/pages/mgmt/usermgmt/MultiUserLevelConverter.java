package com.btxtech.game.wicket.pages.mgmt.usermgmt;

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.tracking.UserTracking;
import com.btxtech.game.wicket.uiservices.LevelReadonlyPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 03.01.14
 * Time: 18:43
 */
public class MultiUserLevelConverter extends MgmtWebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private UserService userService;
    private List<UserEntryHelper> userEntryHelpers = new ArrayList<>();
    private Integer currentLevelId;
    private Integer newLevelId;

    public MultiUserLevelConverter() {
        add(new FeedbackPanel("msgs"));
        // Selector
        Form levelSelectForm = new Form("levelSelectForm");
        add(levelSelectForm);
        levelSelectForm.add(new TextField<>("currentLevelId", new IModel<Integer>() {

            @Override
            public void detach() {
                // Ignore
            }

            @Override
            public Integer getObject() {
                return currentLevelId;
            }

            @Override
            public void setObject(Integer levelId) {
                if (levelId == null) {
                    currentLevelId = null;
                    return;
                }
                try {
                    DbLevel dbLevel = userGuidanceService.getDbLevel(levelId);
                    if (dbLevel != null) {
                        currentLevelId = levelId;
                    } else {
                        currentLevelId = null;
                        error("No such current level: " + levelId);
                    }
                } catch (Exception e) {
                    error("No such current level: " + levelId);
                }
            }
        }, Integer.class));
        levelSelectForm.add(new LevelReadonlyPanel("currentLevel", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return currentLevelId;
            }
        }));
        levelSelectForm.add(new TextField<>("newLevelId", new IModel<Integer>() {

            @Override
            public void detach() {
                // Ignore
            }

            @Override
            public Integer getObject() {
                return newLevelId;
            }

            @Override
            public void setObject(Integer levelId) {
                if (levelId == null) {
                    newLevelId = null;
                    return;
                }
                try {
                    DbLevel dbLevel = userGuidanceService.getDbLevel(levelId);
                    if (dbLevel != null) {
                        newLevelId = levelId;
                    } else {
                        newLevelId = null;
                        error("No such new level: " + levelId);
                    }
                } catch (Exception e) {
                    error("No such new level: " + levelId);
                }
            }
        }, Integer.class));
        levelSelectForm.add(new LevelReadonlyPanel("newLevel", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return newLevelId;
            }
        }));
        levelSelectForm.add(new Button("listUsers") {

            @Override
            public void onSubmit() {
                userEntryHelpers.clear();
                if (currentLevelId == null) {
                    return;
                }
                for (SimpleUser simpleUser : userService.getAllSimpleUsersWithLevel(currentLevelId)) {
                    userEntryHelpers.add(new UserEntryHelper(simpleUser));
                }
            }
        });
        // User table
        Form foundUserForm = new Form("foundUserForm");
        add(foundUserForm);
        foundUserForm.add(new DataView<UserEntryHelper>("foundUserTable", new ListDataProvider<UserEntryHelper>(userEntryHelpers) {
            @Override
            public IModel<UserEntryHelper> model(UserEntryHelper userEntryHelper) {
                return new CompoundPropertyModel<>(userEntryHelper);
            }
        }) {
            @Override
            protected void populateItem(final Item<UserEntryHelper> item) {
                // User link
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(UserTracking.USER_ID, Integer.toString(item.getModelObject().getId()));
                BookmarkablePageLink<UserTracking> link = new BookmarkablePageLink<>("userLink", UserTracking.class, pageParameters);
                link.add(new Label("name"));
                item.add(link);
                // Include
                item.add(new CheckBox("include") {
                    @Override
                    public boolean isVisible() {
                        return newLevelId != null;
                    }
                });
                // Even and odd columns
                if (item.getIndex() % 2 == 0) {
                    item.add(new AttributeModifier("style", "background-color:#f2f2f2"));
                }

            }
        });
        // User count
        foundUserForm.add(new Label("userCount", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (userEntryHelpers.isEmpty()) {
                    return "-";
                } else {
                    return Integer.toString(userEntryHelpers.size());
                }
            }
        }));
        // Convert button
        foundUserForm.add(new Button("convertUsers") {

            @Override
            public void onSubmit() {
                if (newLevelId != null) {
                    for (UserEntryHelper userEntryHelper : userEntryHelpers) {
                        if (userEntryHelper.isInclude()) {
                            try {
                                userGuidanceService.promote(userService.getUserState(userService.getUser(userEntryHelper.getId())), newLevelId);
                            } catch (Exception e) {
                                ExceptionHandler.handleException(e, "Unable to convert user: " + userEntryHelper.getName() + " (" + userEntryHelper.getId() + ")");
                            }
                        }
                    }
                    userEntryHelpers.clear();
                }
            }

            @Override
            public boolean isVisible() {
                return newLevelId != null;
            }
        });

    }
}
