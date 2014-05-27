package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.mgmt.ClientPerfmonDto;
import com.btxtech.game.services.mgmt.MgmtService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 17:01
 */
public class ClientPerfmonTable extends MgmtWebPage {
    @SpringBean
    private MgmtService mgmtService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);

    public ClientPerfmonTable() {
        add(new FeedbackPanel("msgs"));
        ListView<ClientPerfmonDto> listView = new ListView<ClientPerfmonDto>("clientPerfmonData", new AbstractReadOnlyModel<List<ClientPerfmonDto>>() {
            @Override
            public List<ClientPerfmonDto> getObject() {
                return mgmtService.getClientPerfmonData();
            }
        }) {
            @Override
            protected void populateItem(ListItem<ClientPerfmonDto> listItem) {
                listItem.add(new Label("lastActivated", simpleDateFormat.format(listItem.getModelObject().getLastActivated())));
                listItem.add(new Label("time", DateUtil.formatDuration(listItem.getModelObject().getTotalTime())));
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(ClientPerfmonDetail.SESSION_KEY, listItem.getModelObject().getSessionId());
                BookmarkablePageLink<ClientPerfmonDetail> link = new BookmarkablePageLink<>("sessionIdLink", ClientPerfmonDetail.class, pageParameters);
                link.add(new Label("sessionId", listItem.getModelObject().getSessionId()));
                listItem.add(link);
            }
        };
        add(listView);
    }

}
