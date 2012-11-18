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
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.ExceptionDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GwtCommon {
    public static final String DEBUG_CATEGORY_DEFINITELY_KILL = "CAN_NOT_DEFINITELY_KILL";
    public static final String DEBUG_CATEGORY_IMAGE_LOADER = "IMAGE_LOADER";
    public static final String DEBUG_CATEGORY_TELEPORTATION = "TELEPORTATION";
    private static ExceptionDialog exceptionDialog;
    private static Boolean isIe6;
    private static Boolean isIe;
    private static Boolean isOpera;
    private static Logger log = Logger.getLogger(GwtCommon.class.getName());
    private static final Set<String> sentIntCheckErrors = new HashSet<String>();

    public static void setUncaughtExceptionHandler() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable t) {
                handleException("GWT uncaught exception handler", t);
            }
        });
    }

    public static boolean checkAndReportHttpStatusCode0(String message, Throwable t) {
        if (t instanceof StatusCodeException && ((StatusCodeException) t).getStatusCode() == 0) {
            sendLogViaLoadScriptCommunication("HTTP status code 0 detected: " + message);
            return true;
        } else {
            return false;
        }
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
        if (showDialog) {
            if (exceptionDialog != null) {
                exceptionDialog.hide(true);
            }
            exceptionDialog = new ExceptionDialog(t);
            DialogManager.showDialog(exceptionDialog, DialogManager.Type.PROMPTLY);
        }
        if (!GWT.isProdMode()) {
            t.printStackTrace();
        }
        sendExceptionToServer(message, t);
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

    public static String setupStackTrace(String message, Throwable throwable) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(See GWT log for stack trace) ");
            if (message != null) {
                stringBuilder.append(message);
                stringBuilder.append(" ");
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
            builder.append(" Caused by: ");
        } else {
            builder.append(" ");
        }
        builder.append(throwable.toString());
    }

    public static void sendDebug(String category, String message) {
        try {
            Connection.getInstance().sendDebug(new Date(), category, message);
            return;
        } catch (Throwable ignore) {
            // Ignore
        }
        sendLogViaLoadScriptCommunication("Can not send debug message to server: ");
    }

    public static void sendLogViaLoadScriptCommunication(String logMessage) {
        try {
            ImageElement imageElement = Document.get().createImageElement();
            imageElement.setSrc("/spring/lsc?" + Constants.ERROR_KEY + "=" + logMessage + "&t=" + System.currentTimeMillis());
            Document.get().appendChild(imageElement);
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

    public static boolean isIE() {
        if (isIe == null) {
            isIe = Window.Navigator.getUserAgent().toLowerCase().contains("msie");
        }
        return isIe;
    }

    public static native void disableBrowserContextMenuJSNI() /*-{
        $doc.oncontextmenu = function () {
            return false;
        };
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

    public static void preventNativeSelection(Widget widget) {
        // Does not work before IE10. Opera support is unknown
        Style style = widget.getElement().getStyle();
        style.setProperty("WebkitUserSelect", "none");
        style.setProperty("KhtmlUserSelect", "none");
        style.setProperty("MozUserSelect", "none");
        style.setProperty("MsUserSelect", "none");
        style.setProperty("OUserSelect", "none");
        style.setProperty("UserSelect", "none");
    }

    public static void preventDragImage(Image image) {
        image.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
            }
        });
    }

    /**
     * If an int has a decimal value e.g. 4.333 this leads to error in de-serialization on the server
     * <br /><br />
     * &nbsp;&nbsp;public native int getNativeInt() &#47;*-{<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;return 4.333; // leads error during de-serialization on the server<br />
     * &nbsp;&nbsp;}-*&#47;;<br /><br />
     *
     * @param intObject Integer to check
     * @return Integer in case of decimal floor will be called
     */
    public static Integer checkInt(Integer intObject, String message) {
        if (intObject == null) {
            return null;
        }
        try {
            if (Math.floor(intObject) == intObject) {
                return intObject;
            } else {
                if (!sentIntCheckErrors.contains(message)) {
                    sentIntCheckErrors.add(message);
                    throw new NumberFormatException("Integer expected but received: " + intObject);
                }
            }
        } catch (NumberFormatException numberFormatException) {
            log.log(Level.WARNING, message, numberFormatException);
        }
        return (int) Math.floor(intObject);
    }

    /**
     * See checkInt
     * @param integer to correct
     * @return corrected integer
     */
    public static int correctInt(int integer) {
        return (int) Math.floor(integer);
    }

    
    public static void dumpStackToServer(String message) {
        log.log(Level.SEVERE, "Dump Stack: " + message, new Exception("StackTrace"));
    }

    public static Integer getUrlIntegerParameter(String key) {
        String valueString = Window.Location.getParameter(key);
        if (valueString == null) {
            return null;
        }
        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
