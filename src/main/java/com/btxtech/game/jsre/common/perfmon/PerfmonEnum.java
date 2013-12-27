package com.btxtech.game.jsre.common.perfmon;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 15:00
 */
public enum PerfmonEnum {
    IGNORE("IGNORE"),
    ACTION_HANDLER("Action"),
    PACKET_INFO_HANDLING("Packets"),
    QUEST_PROGRESS_COCKPIT_RADAR_HINT("QPC Radar Hint"),
    SIMULATION_CONDITION_SERVICE("Condition"),
    CLIENT_USER_TRACKING("User Tracking"),
    INFORMATION_COCKPIT("Info Cockpit"),
    IMAGE_LOADER("Image Loader"),
    RADAR_FRAME_VIEW("Radar Frame"),
    SYNC_POLL("Sync Poll"),
    RADAR_ITEM_VIEW("Radar Item"),
    SCROLL("Scroll"),
    SPEECH_SHOW("Speech show"),
    SPEECH_HIDE("Speech hide"),
    SIMULATION("Simulation"),
    CHAT_START("Chat start"),
    ITEM_CONTAINER("Item Container"),
    PERFMON("Permon"),
    TIP_MANAGER_SHOW("Tip Manager show"),
    REGISTER_DIALOG("Register Dialog"),
    STARTUP_FADE_OUT("Start Fade out"),
    STARTUP_FADE_IN("Start Fade in"),
    BOT_RUNNER("Bot runner"),
    BOT_THREAD("Bot thread"),
    CHAT_POLL("Chat poll"),
    TIP_MANAGER_HIDE("Tip Manager hide"),
    TIP_ARROW("Tip Arrow"),
    SYNC_HANDLE_PACKETS("Handle Packets"),
    TERRAIN_MOUSE_MOVE("Terrain Mouse move"),
    TERRAIN_MOUSE_DOWN("Terrain Mouse down"),
    TERRAIN_MOUSE_UP("Terrain Mouse up"),
    MAP_WINDOW_EVENT_PREVIEW("Map Window Event preview"),
    ITEM_MOUSE_DOWN("Item Mouse down"),
    ITEM_MOUSE_OVER("Item Mouse over"),
    ITEM_MOUSE_OUT("Item Mouse out"),
    ITEM_MOUSE_UP("Item Mouse up"),
    RENDERER("Renderer"),
    RENDERER_OVERLAY("Renderer Overlay"),
    RADAR_ATTACK_VISUALISATION("Radar Attack Visualisation");

    private String displayName;

    PerfmonEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
