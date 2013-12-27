package com.btxtech.game.wicket;

import com.btxtech.game.services.user.UserService;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.GrantedAuthority;

/**
 * User: beat
 * Date: 27.02.2011
 * Time: 13:19:09
 */
public class WicketAuthenticatedWebSession extends AuthenticatedWebSession {
    @SpringBean
    private UserService userService;
    private String trackingCookieId;
    private boolean isTrackingCookieIdCookieNeeded;
    private boolean newUserTracking;

    public WicketAuthenticatedWebSession(Request request) {
        super(request);
        // If not bound, two session object will be generated
        bind();
        Injector.get().inject(this);
        handleCookieTracking(request);
    }

    @Override
    public boolean authenticate(String userName, String password) {
        return userService.login(userName, password);
    }

    public void setSignIn(boolean signedIn) {
        signIn(signedIn);
    }

    @Override
    public void signOut() {
        super.signOut();
        userService.logout();
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        if (isSignedIn()) {
            for (GrantedAuthority grantedAuthority : userService.getAuthorities()) {
                roles.add(grantedAuthority.getAuthority());
            }
        }
        return roles;
    }

    public String getTrackingCookieId() {
        return trackingCookieId;
    }

    public boolean isNewUserTracking() {
        return newUserTracking;
    }

    public boolean isTrackingCookieIdCookieNeeded() {
        return isTrackingCookieIdCookieNeeded;
    }

    public void clearTrackingCookieIdCookieNeeded() {
        isTrackingCookieIdCookieNeeded = false;
    }

    private void handleCookieTracking(Request request) {
        trackingCookieId = WebCommon.getTrackingCookie(((WebRequest) request).getCookies());
        isTrackingCookieIdCookieNeeded = true;
        if (trackingCookieId != null) {
            newUserTracking = false;
        } else {
            trackingCookieId = WebCommon.generateTrackingCookieId();
            newUserTracking = true;
        }
    }
}
