package com.btxtech.game.services.gwt;

import com.btxtech.game.services.common.Utils;
import com.google.gwt.logging.server.RemoteLoggingServiceImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 16:37:37
 */
public class ExtendedRemoteLoggingServiceImpl extends RemoteLoggingServiceImpl {
    private static final String SYMBOL_PARAMETER_MAP_PROD = "symbolMapsDirectoryProd";
    private static final String SYMBOL_PARAMETER_MAP_DEV = "symbolMapsDirectoryDev";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (Utils.isTestModeStatic()) {
            if (getInitParameter(SYMBOL_PARAMETER_MAP_DEV) != null && !getInitParameter(SYMBOL_PARAMETER_MAP_DEV).trim().isEmpty()) {
                setSymbolMapsDirectory(config.getServletContext().getRealPath(getInitParameter(SYMBOL_PARAMETER_MAP_DEV)));
            }
        } else {
            if (getInitParameter(SYMBOL_PARAMETER_MAP_PROD) != null && !getInitParameter(SYMBOL_PARAMETER_MAP_PROD).trim().isEmpty()) {
                setSymbolMapsDirectory(config.getServletContext().getRealPath(getInitParameter(SYMBOL_PARAMETER_MAP_PROD)));
            }
        }
    }
}
