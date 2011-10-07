package com.btxtech.game.services.gwt;

import com.google.gwt.logging.server.RemoteLoggingServiceUtil;
import com.google.gwt.logging.server.StackTraceDeobfuscator;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private Log log = LogFactory.getLog(ExtendedRemoteLoggingServiceImpl.class);

    // No deobfuscator by default
    private static StackTraceDeobfuscator deobfuscator = null;
    private static String loggerNameOverride = null;


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
            log.error("User Agent: " + perThreadRequest.get().getHeader("user-agent"));
            log.error("Session Id: " + perThreadRequest.get().getSession().getId());
            RemoteLoggingServiceUtil.logOnServer(lr, strongName, deobfuscator, loggerNameOverride);
        } catch (RemoteLoggingServiceUtil.RemoteLoggingException e) {
            log.error("Remote logging failed", e);
            return "Remote logging failed, check stack trace for details.";
        }
        return null;
    }

    /**
     * By default, messages are logged to a logger that has the same name as
     * the logger that created them on the client. If you want to log all messages
     * from the client to a logger with another name, you can set the override
     * using this method.
     *
     * @param override loggerNameOverride
     */
    public void setLoggerNameOverride(String override) {
        loggerNameOverride = override;
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

}