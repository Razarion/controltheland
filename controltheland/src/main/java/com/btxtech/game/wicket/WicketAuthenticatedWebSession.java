package com.btxtech.game.wicket;

import com.btxtech.game.services.user.UserService;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.injection.web.InjectorHolder;
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

    public WicketAuthenticatedWebSession(Request request) {
        super(request);
        // Inject AuthenticationManager
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    public boolean authenticate(String userName, String password) {
        return userService.login(userName, password);
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
}
