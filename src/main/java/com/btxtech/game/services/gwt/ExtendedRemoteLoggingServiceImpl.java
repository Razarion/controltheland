package com.btxtech.game.services.gwt;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.UserService;
import com.google.gwt.logging.server.StackTraceDeobfuscator;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.logging.LogRecord;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 16:37:37
 */
public class ExtendedRemoteLoggingServiceImpl extends RemoteServiceServlet implements RemoteLoggingService {
    private static final String SYMBOL_PARAMETER_MAP = "symbolMapsDirectory";
    @Autowired
    private UserService userService;
    private Log log = LogFactory.getLog(ExtendedRemoteLoggingServiceImpl.class);
    private static StackTraceDeobfuscator deobfuscator = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (getInitParameter(SYMBOL_PARAMETER_MAP) != null && !getInitParameter(SYMBOL_PARAMETER_MAP).trim().isEmpty()) {
            setSymbolMapsDirectory(config.getServletContext().getRealPath(getInitParameter(SYMBOL_PARAMETER_MAP)));
        }
    }

    /**
     * Logs a Log Record which has been serialized using GWT RPC on the server.
     *
     * @return either an error message, or null if logging is successful.
     */
    @Override
    public String logOnServer(LogRecord lr) {
        String strongName = getPermutationStrongName();
        try {
            log.error("-----------------ExtendedRemoteLoggingServiceImpl-----------------");
            ExceptionHandler.logParameters(log, userService);
            if (deobfuscator != null) {
                lr = deobfuscator.deobfuscateLogRecord(lr, strongName);
            }
            log.error(lr.getMessage(), lr.getThrown());
        } catch (Exception e) {
            log.error("Remote logging failed", e);
            return "Remote logging failed, check stack trace for details.";
        }
        return null;
    }

    /**
     * By default, this service does not do any deobfuscation. In order to do
     * server side deobfuscation, you must copy the symbolMaps files to a
     * directory visible to the server and set the directory using this method.
     *
     * @param symbolMapsDir symbolMapsDir
     */
    public void setSymbolMapsDirectory(String symbolMapsDir) {
        if (deobfuscator == null) {
            deobfuscator = new StackTraceDeobfuscator(symbolMapsDir);
        } else {
            deobfuscator.setSymbolMapsDirectory(symbolMapsDir);
        }
    }

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