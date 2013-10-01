package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.NameErrorPair;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public abstract class CheckUserEmailPanel extends Panel {
    @SpringBean
    private UserService userService;

    public CheckUserEmailPanel(String id) {
        super(id);
        add(new DataView<NameErrorPair>("table", new DetachHashListProvider<NameErrorPair>() {
            @Override
            protected List<NameErrorPair> createList() {
                return userService.checkUserEmails(getUsers());
            }
        }) {
            @Override
            protected void populateItem(final Item<NameErrorPair> item) {
                item.add(new Label("name"));
                item.add(new Label("error"));
            }
        });
    }

    abstract public String getUsers();
}
