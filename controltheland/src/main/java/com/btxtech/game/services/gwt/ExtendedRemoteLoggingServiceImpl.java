package com.btxtech.game.services.gwt;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 16:37:37
 */
public class ExtendedRemoteLoggingServiceImpl extends RemoteLoggingServiceImpl {
    private static final String SYMBOL_PARAMETER_MAP = "symbolMapsDirectory";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (getInitParameter(SYMBOL_PARAMETER_MAP) != null && !getInitParameter(SYMBOL_PARAMETER_MAP).trim().isEmpty()) {
            setSymbolMapsDirectory(config.getServletContext().getRealPath(getInitParameter(SYMBOL_PARAMETER_MAP)));
        }
    }
}
