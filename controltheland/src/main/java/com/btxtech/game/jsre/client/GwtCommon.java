/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.dialogs.ExceptionDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;
import java.util.Date;

public class GwtCommon {
    private static ExceptionDialog exceptionDialog;
    private static Boolean isIe6;
    private static Boolean isOpera;

    public static void setUncaughtExceptionHandler() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable t) {
                handleException(t);
            }
        });
    }

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
        if (t instanceof StatusCodeException) {
            System.out.println("StatusCodeException status code: " + ((StatusCodeException) t).getStatusCode());
        }
        t.printStackTrace();
        if (showDialog) {
            if (exceptionDialog != null) {
                exceptionDialog.hide(true);
            }
            exceptionDialog = new ExceptionDialog(t);
        }
        sendExceptionToServer(message, t);
    }

    private static void sendExceptionToServer(String message, Throwable throwable) {
        sendLogToServer(setupStackTrace(message, throwable));
    }

    public static String setupStackTrace(String message, Throwable throwable) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            if (message != null) {
                stringBuilder.append(message);
                stringBuilder.append("\n");
            }
            boolean isCause = false;
            while (true) {
                setupStackTrace(stringBuilder, throwable, isCause);
                Throwable inner = throwable.getCause();
                if (inner == null || inner == throwable) {
                    break;
                }
                throwable = inner;
                isCause = true;
            }
            return stringBuilder.toString();
        } catch (Throwable ignore) {
            return "failed to setup stacktrace: " + ignore;
        }
    }

    public static void setupStackTrace(StringBuilder builder, Throwable throwable, boolean isCause) {
        if (isCause) {
            builder.append("Caused by: ");
        }
        if (throwable instanceof StatusCodeException) {
            builder.append("StatusCodeException status code: ");
            builder.append(((StatusCodeException) throwable).getStatusCode());
            builder.append("\n");
        }
        builder.append(throwable.getMessage());
        builder.append("\n");
        builder.append(throwable.toString());
        builder.append("\n");
        for (Object element : throwable.getStackTrace()) {
            builder.append("  at ");
            builder.append(element.toString());
            builder.append("\n");
        }
    }

    public static void sendLogToServer(String logMessage) {
        System.out.println(logMessage);
        Connection.getInstance().log(logMessage, new Date());
        try {
            if (Connection.isConnected()) {
                Connection.getInstance().log(logMessage, new Date());
                return;
            }
        } catch (Throwable ignore) {
            // Ignore
        }
        sendLogViaLoadScriptCommunication(logMessage);
    }

    public static void sendLogViaLoadScriptCommunication(String logMessage) {
        try {
            ScriptElement scriptElement = Document.get().createScriptElement();
            scriptElement.setType("text/javascript");
            scriptElement.setSrc("/spring/lsc?" + Constants.ERROR_KEY + "=" + logMessage);
            Document.get().getElementsByTagName("head").getItem(0).appendChild(scriptElement);
        } catch (Throwable ignore) {
            // Ignore
        }
    }

    public static boolean isIe6() {
        if (isIe6 == null) {
            isIe6 = Window.Navigator.getUserAgent().contains("msie 6");
        }
        return isIe6;
    }

    public static boolean isOpera() {
        if (isOpera == null) {
            isOpera = Window.Navigator.getUserAgent().contains("Opera");
        }
        return isOpera;
    }

    public static native void disableBrowserContextMenuJSNI() /*-{
        $doc.oncontextmenu = function() { return false; };
    }-*/;

    /*
     * Just to prevent image dragging and selection
     */
    public static void preventDefault(DomEvent event) {
        event.stopPropagation();
        event.preventDefault();
    }

    native public static void closeWindow()/*-{
        $wnd.close(); 
    }-*/;


    public static void stopPropagation(ExtendedAbsolutePanel absolutePanel) {
        absolutePanel.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        });
        absolutePanel.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.stopPropagation();
            }
        });
    }
}
