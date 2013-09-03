package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 12.07.13
 * Time: 15:52
 */
public class RazarionPage extends WebPage {
    public static final String HTML5_KEY = "html5";
    public static final String HTML5_KEY_N = "n";
    public static final String HTML5_KEY_Y = "y";
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private CmsUiService cmsUiService;

    public RazarionPage(PageParameters parameters) {
        super(parameters);
    }

    public RazarionPage() {
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        // LSC detection
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("DISPLAY_ERROR_PREFIX", getClass().getSimpleName());
        response.render(new JavaScriptContentHeaderItem(new PackageTextTemplate(Game.class, "LscErrorHandler.js").asString(parameters), null, null));
        // Javascript detection
        if (!userTrackingService.isJavaScriptDetected()) {
            parameters = new HashMap<>();
            parameters.put("HTML5_KEY", HTML5_KEY);
            parameters.put("HTML5_KEY_Y", HTML5_KEY_Y);
            parameters.put("HTML5_KEY_N", HTML5_KEY_N);
            response.render(new OnLoadHeaderItem(new PackageTextTemplate(Game.class, "JavascriptDetection.js").asString(parameters)));
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        WicketAuthenticatedWebSession wicketSession = (WicketAuthenticatedWebSession) getSession();
        if (wicketSession.isTrackingCookieIdCookieNeeded()) {
            WebCommon.setTrackingCookie((WebResponse) getResponse(), wicketSession.getTrackingCookieId());
            wicketSession.clearTrackingCookieIdCookieNeeded();
        }
    }
}
