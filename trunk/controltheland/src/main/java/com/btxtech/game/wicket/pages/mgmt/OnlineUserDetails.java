package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.mgmt.tracking.SessionDetail;
import com.btxtech.game.wicket.pages.mgmt.tracking.UserTracking;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: 02.04.13
 * Time: 16:04
 */
public class OnlineUserDetails extends MgmtWebPage {
    @SpringBean
    private PlanetSystemService planetSystemService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private ServerGlobalConnectionService connectionService;


    public OnlineUserDetails() {
        setupRealGameTable();
        setupMissionTable();
        setupForm();
    }

    private void setupRealGameTable() {
        DetachHashListProvider<SimpleBase> provider = new DetachHashListProvider<SimpleBase>() {
            @Override
            protected List<SimpleBase> createList() {
                return planetSystemService.getAllOnlineBases();
            }
        };

        add(new DataView<SimpleBase>("realGameTable", provider) {
            @Override
            protected void populateItem(final Item<SimpleBase> item) {
                ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(item.getModelObject());
                // Planet name
                item.add(new Label("planet", serverPlanetServices.getPlanetInfo().getName()));
                UserState userState = userService.getUserState(item.getModelObject());
                // User Link
                if (userState.isRegistered()) {
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add(UserTracking.USER_ID, Integer.toString(userState.getUser()));
                    BookmarkablePageLink<UserTracking> link = new BookmarkablePageLink<>("userLink", UserTracking.class, pageParameters);
                    link.add(new Label("userName", userService.getUserName(userState)));
                    item.add(link);
                    item.add(new Label("baseName").setVisible(false));
                } else {
                    item.add(new BookmarkablePageLink<UserTracking>("userLink", UserTracking.class).setVisible(false));
                    item.add(new Label("baseName", serverPlanetServices.getBaseService().getBaseName(item.getModelObject())));
                }
                // Session link
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(SessionDetail.SESSION_KEY, userState.getSessionId());
                BookmarkablePageLink<SessionDetail> link = new BookmarkablePageLink<>("sessionLink", SessionDetail.class, pageParameters);
                link.add(new Label("sessionId", userState.getSessionId()));
                item.add(link);
            }
        });
    }

    private void setupMissionTable() {
        DetachHashListProvider<UserState> provider = new DetachHashListProvider<UserState>() {
            @Override
            protected List<UserState> createList() {
                return connectionService.getAllOnlineMissionUserState();
            }
        };

        add(new DataView<UserState>("missionTable", provider) {
            @Override
            protected void populateItem(final Item<UserState> item) {
                // Mission name
                DbLevelTask dbLevelTask = userGuidanceService.getActiveQuest(item.getModelObject());
                item.add(new Label("mission", dbLevelTask != null && dbLevelTask.getDbTutorialConfig() != null ? dbLevelTask.getDbTutorialConfig().getName() : "???"));

                // User Link
                if (item.getModelObject().isRegistered()) {
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add(UserTracking.USER_ID, Integer.toString(item.getModelObject().getUser()));
                    BookmarkablePageLink<UserTracking> link = new BookmarkablePageLink<>("userLink", UserTracking.class, pageParameters);
                    link.add(new Label("userName", userService.getUserName(item.getModelObject())));
                    item.add(link);
                    item.add(new Label("baseName").setVisible(false));
                } else if (planetSystemService.hasPlanet(item.getModelObject())) {
                    ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(item.getModelObject());
                    item.add(new BookmarkablePageLink<UserTracking>("userLink", UserTracking.class).setVisible(false));
                    item.add(new Label("baseName", serverPlanetServices.getBaseService().getBaseName(serverPlanetServices.getBaseService().getBase(item.getModelObject()).getSimpleBase())));
                } else {
                    item.add(new BookmarkablePageLink<UserTracking>("userLink", UserTracking.class).setVisible(false));
                    item.add(new Label("baseName").setVisible(false));
                }
                // Session link
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(SessionDetail.SESSION_KEY, item.getModelObject().getSessionId());
                BookmarkablePageLink<SessionDetail> link = new BookmarkablePageLink<>("sessionLink", SessionDetail.class, pageParameters);
                link.add(new Label("sessionId", item.getModelObject().getSessionId()));
                item.add(link);
            }
        });
    }

    private void setupForm() {
        // Just for handling the reload
        add(new Form("form"));
    }
}
