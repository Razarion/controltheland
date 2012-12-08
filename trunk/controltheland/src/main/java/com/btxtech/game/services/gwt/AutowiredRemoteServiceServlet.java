package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.InvalidNickName;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;

/**
 * User: beat
 * Date: 07.03.2012
 * Time: 15:58:30
 */
public abstract class AutowiredRemoteServiceServlet extends RemoteServiceServlet {
    @Override
    public void init() throws ServletException {
        super.init();

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        if (ctx == null) {
            throw new IllegalStateException("No Spring web application");
        }

        // wire the bean
        ctx.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }
}
