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

import com.btxtech.game.jsre.client.dialogs.ExceptionDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
        handleException(t, false);
    }

    public static void handleException(Throwable t, boolean showDialog) {
        t.printStackTrace();
        if (showDialog) {
            if (exceptionDialog != null) {
                exceptionDialog.hide(true);
            }
            exceptionDialog = new ExceptionDialog(t);
        }
        sendExceptionToServer(t);
    }


    private static void sendExceptionToServer(Throwable throwable) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
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
            sendLogToServer(stringBuilder.toString());
        } catch (Throwable ignore) {
            // Ignore
        }
    }

    public static void sendLogToServer(String logMessage) {
        System.out.println(logMessage);
        try {
            if (Connection.isConnected()) {
                Connection.getMovableServiceAsync().log(logMessage, new Date(), new AsyncCallback() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //Ignore
                    }

                    @Override
                    public void onSuccess(Object result) {
                        //Ignore
                    }
                });
            }
        } catch (Throwable ignore) {
            // Ignore
        }
    }

    private static void setupStackTrace(StringBuilder builder, Throwable throwable, boolean isCause) {
        if (isCause) {
            builder.append("Caused by: ");
        }
        builder.append(throwable.toString());
        builder.append("\n");
        for (Object element : throwable.getStackTrace()) {
            builder.append("  at ");
            builder.append(element.toString());
            builder.append("\n");
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
    public static void preventDefault(MouseEvent event) {
        event.stopPropagation();
        event.preventDefault();
    }

    native public static void closeWindow()/*-{
        $wnd.close(); 
    }-*/;
}
