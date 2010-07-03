/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.wicket.pages.basepage;

import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.forum.ForumView;
import com.btxtech.game.wicket.pages.home.Home;
import com.btxtech.game.wicket.pages.info.Info;
import com.btxtech.game.wicket.pages.market.MarketPage;
import com.btxtech.game.wicket.pages.statistics.StatisticsPage;
import com.btxtech.game.wicket.pages.user.LoggedinBox;
import com.btxtech.game.wicket.pages.user.LoginBox;
import com.btxtech.game.wicket.pages.user.UserListPage;
import java.io.Serializable;
import java.util.ArrayList;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 10:05:20 PM
 */
public class BasePage extends WebPage implements IHeaderContributor {
    public static final String JAVA_SCRIPT_DETECTION = "var f = document.createElement('script');\n" +
            "f.setAttribute(\"type\", \"text/javascript\");\n" +
            "f.setAttribute(\"src\", \"/spring/statJS\");\n" +
            "document.getElementsByTagName(\"head\")[0].appendChild(f)";

    @SpringBean
    private UserService userService;
    @SpringBean
    private UserTrackingService userTrackingService;

    public BasePage(PageParameters pageParameters) {
        super(pageParameters);
        // TODO this is ugly
        if (userService.isLoggedin()) {
            add(new LoggedinBox());
        } else {
            add(new LoginBox());
        }

        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add(new MenuItem("home", Home.class, this));
        menuItems.add(new MenuItem("info", Info.class, this));
        menuItems.add(new MenuItem("market", MarketPage.class, this));
        menuItems.add(new MenuItem("users", UserListPage.class, this));
        menuItems.add(new MenuItem("statistics", StatisticsPage.class, this));
        menuItems.add(new MenuItem("forum", ForumView.class, this));
        //menuItems.add(new MenuItem("mgmt", MgmtPage.class, this));
        buildMenu(menuItems);
    }

    public BasePage() {
        this(null);
    }

    private void buildMenu(ArrayList<MenuItem> menuItems) {
        ListView<MenuItem> history = new ListView<MenuItem>("menu", menuItems) {
            protected void populateItem(final ListItem<MenuItem> linkItem) {
                BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("link", linkItem.getModelObject().destination);
                if (linkItem.getModelObject().isSelected()) {
                    SimpleAttributeModifier classModifier = new SimpleAttributeModifier("class", "menuItemSelected");
                    linkItem.add(classModifier);
                }
                link.add(new Label("linkName", linkItem.getModelObject().getName()));
                linkItem.add(link);
            }
        };
        history.setReuseItems(true);
        add(history);

    }

    @Override
    protected void onBeforeRender() {
        userTrackingService.pageAccess(this);

        // TODO this is ugly
        remove("signinBox");
        if (userService.isLoggedin()) {
            add(new LoggedinBox());
        } else {
            add(new LoginBox());
        }
        super.onBeforeRender();
    }

    public String getAdditionalPageInfo() {
        return null;
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        if (!userTrackingService.isJavaScriptDetected()) {
            iHeaderResponse.renderJavascript(JAVA_SCRIPT_DETECTION, null);
        }
    }

    class MenuItem implements Serializable {
        private Class<? extends Page> destination;
        private String name;
        private boolean isSelected;

        MenuItem(String name, Class<? extends Page> destination, Object thisObject) {
            this.destination = destination;
            this.name = name;
            isSelected = destination.equals(thisObject.getClass());
        }

        public Class<? extends Page> getDestination() {
            return destination;
        }

        public String getName() {
            return name;
        }

        public boolean isSelected() {
            return isSelected;
        }
    }
}
