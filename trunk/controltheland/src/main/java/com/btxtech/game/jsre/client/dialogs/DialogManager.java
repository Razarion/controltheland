package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 19.03.2011
 * Time: 09:40:34
 */
public class DialogManager implements CloseHandler<PopupPanel> {
    private static final DialogManager INSTANCE = new DialogManager();

    public enum Type {
        PROMPTLY,
        STACK_ABLE,
        QUEUE_ABLE,
        UNIMPORTANT
    }

    private Dialog activeDialog;
    private List<Dialog> dialogQueue = new ArrayList<Dialog>();
    private List<Dialog> stackedDialogs = new ArrayList<Dialog>();

    public static void showDialog(Dialog dialog, Type type) {
        INSTANCE.showDialogPrivate(dialog, type);
    }

    public static void showDialog(final DialogUiBinderWrapper dialogUiBinderWrapper, Type type) {
        Dialog dialog = new Dialog(dialogUiBinderWrapper.getDialogTitle()) {
            @Override
            protected void setupPanel(VerticalPanel dialogVPanel) {
                dialogVPanel.add(dialogUiBinderWrapper);
            }
        };
        dialogUiBinderWrapper.setAndInit(dialog);
        showDialog(dialog, type);
    }

    public void showDialogPrivate(Dialog dialog, Type type) {
        if (activeDialog == null) {
            showDialog(dialog);
        } else {
            switch (type) {
                case PROMPTLY:
                    activeDialog.close();
                    showDialog(dialog);
                    break;
                case QUEUE_ABLE:
                    dialogQueue.add(dialog);
                    break;
                case STACK_ABLE:
                    showStackedDialog(dialog);
                    break;
                case UNIMPORTANT:
                    break;
                default:
                    throw new IllegalArgumentException("Unknown dialog type: " + type);
            }
        }
    }

    private void showDialog(Dialog dialog) {
        dialog.addCloseHandler(this);
        activeDialog = dialog;
        activeDialog.setupDialog();
    }

    private void showStackedDialog(Dialog dialog) {
        dialog.addCloseHandler(this);
        stackedDialogs.add(dialog);
        dialog.setupDialog();
        ClientUserTracker.getInstance().onDialogAppears(dialog, "Dialog");
    }

    @Override
    public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
        Dialog dialog = (Dialog) popupPanelCloseEvent.getTarget();
        if (dialog.equals(activeDialog)) {
            removeAllStackedDialogs();
            activeDialog = null;
            if (dialogQueue.isEmpty()) {
                return;
            }
            showDialog(dialogQueue.remove(0));
        } else if (stackedDialogs.contains(dialog)) {
            stackedDialogs.remove(dialog);
        }
        ClientUserTracker.getInstance().onDialogDisappears(dialog);
    }

    private void removeAllStackedDialogs() {
        // Prevent concurrent modification exception due to the close callback
        while (!stackedDialogs.isEmpty()) {
            stackedDialogs.remove(0).close();
        }
    }


}
