package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.InvitationService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.07.13
 * Time: 17:55
 */
public class InvitationStart extends RazarionPage {
    @SpringBean
    private InvitationService invitationService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public InvitationStart(PageParameters parameters) {
        super(parameters);

        try {
            userTrackingService.pageAccess(getClass().getName(), parameters.toString());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        PageParameters gamePageParameters = new PageParameters();
        if (!userGuidanceService.isStartRealGame()) {
            gamePageParameters.add(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, Integer.toString(userGuidanceService.getDefaultLevelTaskId()));
        }
        setResponsePage(Game.class, gamePageParameters);
    }
}
