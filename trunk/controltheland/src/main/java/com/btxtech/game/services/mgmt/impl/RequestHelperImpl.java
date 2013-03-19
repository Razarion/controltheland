package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.mgmt.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * User: beat
 * Date: 15.01.13
 * Time: 16:53
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestHelperImpl implements RequestHelper {
    @Autowired
    private HttpServletRequest request;

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }
}
