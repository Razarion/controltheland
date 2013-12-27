package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.ExceptionDialog;
import com.btxtech.game.jsre.common.CommonJava;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 29.11.12
 * Time: 23:03
 */
public class ClientExceptionHandler {
    private static ExceptionDialog exceptionDialog;
    private static Logger log = Logger.getLogger(ClientExceptionHandler.class.getName());
    private static Collection<LogElement> alreadyLoggedLogElement = new HashSet<LogElement>();

    public static void handleException(Throwable t) {
        handleException(null, t, false);
    }

    public static void handleException(String message, Throwable t) {
        handleException(message, t, false);
    }

    public static void handleException(Throwable t, boolean showDialog) {
        handleException(null, t, showDialog);
    }

    public static void handleException(String message, Throwable t, boolean showDialog) {
        if (showDialog) {
            if (exceptionDialog != null) {
                exceptionDialog.hide(true);
            }
            exceptionDialog = new ExceptionDialog(t);
            DialogManager.showDialog(exceptionDialog, DialogManager.Type.PROMPTLY);
        }
        // since Logger is used this is not needed anymore
        // if (!GWT.isProdMode()) {
        //    t.printStackTrace();
        // }
        sendExceptionToServer(message, t);
    }

    public static void handleExceptionOnlyOnce(String message, Throwable t) {
        LogElement logElement = new LogElement(t, message);
        if (!alreadyLoggedLogElement.contains(logElement)) {
            handleException("!!Further exception will be suppressed!! " + message, t, false);
            alreadyLoggedLogElement.add(logElement);
        }
    }

    public static void handleExceptionOnlyOnce(Throwable t) {
        handleExceptionOnlyOnce(null, t);
    }

    public static void handleExceptionOnlyOnce(String message) {
        handleExceptionOnlyOnce(message, null);
    }

    private static void sendExceptionToServer(String message, Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        if (throwable != null) {
            builder.append(": ");
            builder.append(throwable.getMessage());
            builder.append(": ");
            builder.append(throwable.getCause());
            builder.append(": ");
            builder.append(throwable.getClass());
        }
        log.log(Level.SEVERE, builder.toString(), throwable);
    }

    private static class LogElement {
        private Throwable throwable;
        private String message;

        private LogElement(Throwable throwable, String message) {
            this.throwable = throwable;
            this.message = message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LogElement that = (LogElement) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;
            if (throwable != null && that.throwable != null) {
                return CommonJava.compareExceptionsDeep(throwable, that.throwable);
            } else if (throwable == null && that.throwable != null) {
                return false;
            } else if (throwable != null && that.throwable == null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = throwable != null ? (throwable.getMessage() != null ? throwable.getMessage().hashCode() : 0) : 0;
            result = 31 * result + (throwable != null ? hashCode(throwable.getStackTrace()) : 0);
            result = 31 * result + (message != null ? message.hashCode() : 0);
            return result;
        }

        private int hashCode(StackTraceElement sA[]) {
            // GWT does not override hasCode() in StackTraceElement
            if (sA == null) {
                return 0;
            }
            int result = 1;
            for (StackTraceElement stackTraceElement : sA) {
                result = 31 * result + (stackTraceElement == null ? 0 : stackTraceElement.toString().hashCode());
            }
            return result;
        }

    }
}
