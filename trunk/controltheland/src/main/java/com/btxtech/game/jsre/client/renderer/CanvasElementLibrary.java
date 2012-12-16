package com.btxtech.game.jsre.client.renderer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 24.08.12
 * Time: 00:40
 */
public class CanvasElementLibrary {
    // Small Arrow
    public static final int SMALL_ARROW_HEIGHT = 15;
    public static final int SMALL_ARROW_WIDTH = 8;
    public static final int SMALL_ARROW_NECK = 3;
    public static final int SMALL_ARROW_HEAD_LENGTH = 10;
    public static final int SMALL_ARROW_LINE_WIDTH = 1;
    public static final int SMALL_ARROW_HEIGHT_TOTAL = SMALL_ARROW_HEIGHT * 2 + 2;
    public static final int SMALL_ARROW_WIDTH_TOTAL = SMALL_ARROW_WIDTH * 2 + 2;
    // Arrow
    public static final int ARROW_HEIGHT = 30;
    public static final int ARROW_WIDTH = 15;
    public static final int ARROW_NECK = 7;
    public static final int ARROW_HEAD_LENGTH = 20;
    public static final int ARROW_LINE_WIDTH = 2;
    public static final int ARROW_HEIGHT_TOTAL = ARROW_HEIGHT * 2 + ARROW_LINE_WIDTH * 2;
    public static final int ARROW_WIDTH_TOTAL = ARROW_WIDTH * 2 + ARROW_LINE_WIDTH * 2;
    // Mouse
    public static final int MOUSE_WIDTH = 45;
    public static final int MOUSE_HEIGHT = 60;
    public static final int MOUSE_LINE_WIDTH = 2;
    public static final int MOUSE_PADDING = 40;
    public static final int MOUSE_WIDTH_TOTAL = MOUSE_WIDTH + 2 * MOUSE_PADDING;
    public static final int MOUSE_HEIGHT_TOTAL = MOUSE_HEIGHT + 2 * MOUSE_PADDING;
    public static final int MOUSE_BUTTON_WIDTH = 15;
    public static final int MOUSE_BUTTON_HEIGHT = 30;
    public static final String MOUSE_PRIMARY_COLOR = "#BDB991";
    public static final String MOUSE_SECONDARY_COLOR = "#B0B0B0";
    public static final String MOUSE_THIRD_COLOR = "#6B6B6B";
    // CORNERS
    public static final int CORNER_LINE_THICKNESS = 2;
    public static final int CORNER_LENGTH = 8;
    public static final int CORNER_TOTAL_LENGTH = 2 * CORNER_LINE_THICKNESS + CORNER_LENGTH;

    private static CanvasElement smallArrowCanvasElement;
    private static CanvasElement arrowCanvasElement;
    private static CanvasElement mouseCanvasElement;
    private static CanvasElement mouseDownCanvasElement;

    private static Map<String, CanvasElement> tlCorners = new HashMap<String, CanvasElement>();
    private static Map<String, CanvasElement> trCorners = new HashMap<String, CanvasElement>();
    private static Map<String, CanvasElement> blCorners = new HashMap<String, CanvasElement>();
    private static Map<String, CanvasElement> brCorners = new HashMap<String, CanvasElement>();


    public static CanvasElement getArrow() {
        if (arrowCanvasElement != null) {
            return arrowCanvasElement;
        }
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(ARROW_WIDTH_TOTAL);
        canvas.setCoordinateSpaceHeight(ARROW_HEIGHT_TOTAL);
        Context2d context2d = canvas.getContext2d();
        context2d.translate(ARROW_WIDTH + ARROW_LINE_WIDTH, ARROW_HEIGHT + ARROW_LINE_WIDTH);
        context2d.setStrokeStyle("#985858");
        context2d.beginPath();
        context2d.moveTo(0, -ARROW_HEIGHT);
        context2d.lineTo(ARROW_WIDTH, ARROW_HEAD_LENGTH - ARROW_HEIGHT);
        context2d.lineTo(ARROW_NECK, ARROW_HEAD_LENGTH - ARROW_HEIGHT);
        context2d.lineTo(ARROW_NECK, ARROW_HEIGHT);
        context2d.lineTo(-ARROW_NECK, ARROW_HEIGHT);
        context2d.lineTo(-ARROW_NECK, ARROW_HEAD_LENGTH - ARROW_HEIGHT);
        context2d.lineTo(-ARROW_WIDTH, ARROW_HEAD_LENGTH - ARROW_HEIGHT);
        context2d.closePath();

        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.setLineJoin(Context2d.LineJoin.ROUND);
        context2d.setLineWidth(ARROW_LINE_WIDTH);
        context2d.stroke();
        context2d.setShadowBlur(0);

        context2d.setFillStyle("#FFFFFF");
        context2d.fill();
        context2d.setLineJoin(Context2d.LineJoin.ROUND);
        context2d.setLineWidth(ARROW_LINE_WIDTH);
        context2d.stroke();
        arrowCanvasElement = canvas.getCanvasElement();
        return arrowCanvasElement;
    }

    public static CanvasElement getSmallArrow() {
        if (smallArrowCanvasElement != null) {
            return smallArrowCanvasElement;
        }
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(SMALL_ARROW_WIDTH_TOTAL);
        canvas.setCoordinateSpaceHeight(SMALL_ARROW_HEIGHT_TOTAL);
        Context2d context2d = canvas.getContext2d();
        context2d.translate(SMALL_ARROW_WIDTH + SMALL_ARROW_LINE_WIDTH, SMALL_ARROW_HEIGHT + SMALL_ARROW_LINE_WIDTH);
        context2d.setStrokeStyle("#985858");
        context2d.beginPath();
        context2d.moveTo(0, -SMALL_ARROW_HEIGHT);
        context2d.lineTo(SMALL_ARROW_WIDTH, SMALL_ARROW_HEAD_LENGTH - SMALL_ARROW_HEIGHT);
        context2d.lineTo(SMALL_ARROW_NECK, SMALL_ARROW_HEAD_LENGTH - SMALL_ARROW_HEIGHT);
        context2d.lineTo(SMALL_ARROW_NECK, SMALL_ARROW_HEIGHT);
        context2d.lineTo(-SMALL_ARROW_NECK, SMALL_ARROW_HEIGHT);
        context2d.lineTo(-SMALL_ARROW_NECK, SMALL_ARROW_HEAD_LENGTH - SMALL_ARROW_HEIGHT);
        context2d.lineTo(-SMALL_ARROW_WIDTH, SMALL_ARROW_HEAD_LENGTH - SMALL_ARROW_HEIGHT);
        context2d.closePath();

        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.setLineJoin(Context2d.LineJoin.ROUND);
        context2d.setLineWidth(SMALL_ARROW_LINE_WIDTH);
        context2d.stroke();
        context2d.setShadowBlur(0);

        context2d.setFillStyle("#FFFFFF");
        context2d.fill();
        context2d.setLineJoin(Context2d.LineJoin.ROUND);
        context2d.setLineWidth(SMALL_ARROW_LINE_WIDTH);
        context2d.stroke();
        smallArrowCanvasElement = canvas.getCanvasElement();
        return smallArrowCanvasElement;
    }

    @Deprecated
    public static CanvasElement getMouse() {
        if (mouseCanvasElement != null) {
            return mouseCanvasElement;
        }
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(MOUSE_WIDTH_TOTAL);
        canvas.setCoordinateSpaceHeight(MOUSE_HEIGHT_TOTAL);
        Context2d context2d = canvas.getContext2d();

        // Create a triangluar path
        context2d.beginPath();
        context2d.moveTo(MOUSE_PADDING, MOUSE_PADDING);
        context2d.quadraticCurveTo(MOUSE_PADDING + MOUSE_WIDTH / 2, MOUSE_PADDING - 15, MOUSE_PADDING + MOUSE_WIDTH, MOUSE_PADDING);
        context2d.quadraticCurveTo(MOUSE_PADDING + MOUSE_WIDTH + 8, MOUSE_PADDING + MOUSE_HEIGHT / 2, MOUSE_PADDING + MOUSE_WIDTH, MOUSE_HEIGHT + MOUSE_PADDING);
        context2d.quadraticCurveTo(MOUSE_PADDING + MOUSE_WIDTH / 2, MOUSE_PADDING + MOUSE_HEIGHT + 30, MOUSE_PADDING, MOUSE_HEIGHT + MOUSE_PADDING);
        context2d.quadraticCurveTo(MOUSE_PADDING - 8, MOUSE_PADDING + MOUSE_HEIGHT / 2, MOUSE_PADDING, MOUSE_PADDING);
        context2d.closePath();

        // Create fill gradient
        CanvasGradient gradient = context2d.createLinearGradient(0, 0, 0, MOUSE_HEIGHT);
        gradient.addColorStop(0, MOUSE_PRIMARY_COLOR);
        gradient.addColorStop(1, MOUSE_SECONDARY_COLOR);

        // Add a shadow around the object
        context2d.setShadowBlur(10);
        context2d.setShadowColor("black");

        // Stroke the outer outline
        context2d.setLineWidth(MOUSE_LINE_WIDTH * 2);
        context2d.setLineJoin(Context2d.LineJoin.ROUND);
        context2d.setStrokeStyle(gradient);
        context2d.stroke();

        // Turn off the shadow, or all future fills will have shadows
        context2d.setShadowBlur(0);

        // Fill the path
        context2d.setFillStyle(gradient);
        context2d.fill();

        // Add a horizon reflection with a gradient to transparent
        gradient = context2d.createLinearGradient(0, MOUSE_PADDING, 0, MOUSE_PADDING + MOUSE_HEIGHT);
        gradient.addColorStop(0, "transparent");
        gradient.addColorStop(0.5, "transparent");
        gradient.addColorStop(0.5, MOUSE_SECONDARY_COLOR);
        gradient.addColorStop(1, MOUSE_THIRD_COLOR);

        context2d.setFillStyle(gradient);
        context2d.fill();

        // Stroke the inner outline
        context2d.setLineWidth(MOUSE_LINE_WIDTH);
        context2d.setLineJoin(Context2d.LineJoin.ROUND);
        context2d.setStrokeStyle("#787878");
        context2d.stroke();

        // Button left
        context2d.beginPath();
        context2d.moveTo(MOUSE_PADDING - 3, MOUSE_PADDING + MOUSE_BUTTON_HEIGHT);
        context2d.lineTo(MOUSE_PADDING + MOUSE_BUTTON_WIDTH, MOUSE_PADDING + MOUSE_BUTTON_HEIGHT);
        context2d.lineTo(MOUSE_PADDING + MOUSE_BUTTON_WIDTH, MOUSE_PADDING - 6);
        context2d.stroke();

        // Button right

        context2d.beginPath();
        context2d.moveTo(MOUSE_PADDING + MOUSE_WIDTH - MOUSE_BUTTON_WIDTH, MOUSE_PADDING - 6);
        context2d.lineTo(MOUSE_PADDING + MOUSE_WIDTH - MOUSE_BUTTON_WIDTH, MOUSE_PADDING + MOUSE_BUTTON_HEIGHT);
        context2d.lineTo(MOUSE_PADDING + MOUSE_WIDTH + 3, MOUSE_PADDING + MOUSE_BUTTON_HEIGHT);
        context2d.stroke();

        mouseCanvasElement = canvas.getCanvasElement();
        return mouseCanvasElement;
    }

    @Deprecated
    public static CanvasElement getMouseButtonDown() {
        if (mouseDownCanvasElement != null) {
            return mouseDownCanvasElement;
        }
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(MOUSE_WIDTH_TOTAL);
        canvas.setCoordinateSpaceHeight(MOUSE_HEIGHT_TOTAL);
        Context2d context2d = canvas.getContext2d();


        // Fill right button
        context2d.beginPath();
        context2d.moveTo(MOUSE_PADDING, MOUSE_PADDING);
        context2d.lineTo(MOUSE_PADDING - 5, MOUSE_PADDING + MOUSE_BUTTON_HEIGHT);
        context2d.lineTo(MOUSE_PADDING + MOUSE_BUTTON_WIDTH, MOUSE_PADDING + MOUSE_BUTTON_HEIGHT);
        context2d.lineTo(MOUSE_PADDING + MOUSE_BUTTON_WIDTH, MOUSE_PADDING - 8);
        context2d.closePath();
        context2d.setFillStyle("#787878");
        context2d.fill();
        mouseDownCanvasElement = canvas.getCanvasElement();
        return mouseDownCanvasElement;
    }

    public static CanvasElement getTlCorner(String color) {
        CanvasElement tlCorner = tlCorners.get(color);
        if (tlCorner == null) {
            tlCorner = createTlCorner(color);
            tlCorners.put(color, tlCorner);
        }
        return tlCorner;
    }

    public static CanvasElement getTrCorner(String color) {
        CanvasElement trCorner = trCorners.get(color);
        if (trCorner == null) {
            trCorner = createTrCorner(color);
            trCorners.put(color, trCorner);
        }
        return trCorner;
    }

    public static CanvasElement getBlCorner(String color) {
        CanvasElement blCorner = blCorners.get(color);
        if (blCorner == null) {
            blCorner = createBlCorner(color);
            blCorners.put(color, blCorner);
        }
        return blCorner;
    }

    public static CanvasElement getBrCorner(String color) {
        CanvasElement brCorner = brCorners.get(color);
        if (brCorner == null) {
            brCorner = createBrCorner(color);
            brCorners.put(color, brCorner);
        }
        return brCorner;
    }

    private static CanvasElement createTlCorner(String color) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        Context2d context2d = canvas.getContext2d();
        context2d.setStrokeStyle(color);
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, CORNER_LENGTH);
        context2d.lineTo(0, 0);
        context2d.lineTo(CORNER_LENGTH, 0);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        return canvas.getCanvasElement();
    }

    private static CanvasElement createTrCorner(String color) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        Context2d context2d = canvas.getContext2d();
        context2d.setStrokeStyle(color);
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, 0);
        context2d.lineTo(CORNER_LENGTH, 0);
        context2d.lineTo(CORNER_LENGTH, CORNER_LENGTH);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        return canvas.getCanvasElement();
    }

    private static CanvasElement createBlCorner(String color) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        Context2d context2d = canvas.getContext2d();
        context2d.setStrokeStyle(color);
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, 0);
        context2d.lineTo(0, CORNER_LENGTH);
        context2d.lineTo(CORNER_LENGTH, CORNER_LENGTH);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        return canvas.getCanvasElement();
    }

    private static CanvasElement createBrCorner(String color) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        Context2d context2d = canvas.getContext2d();
        context2d.setStrokeStyle(color);
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, CORNER_LENGTH);
        context2d.lineTo(CORNER_LENGTH, CORNER_LENGTH);
        context2d.lineTo(CORNER_LENGTH, 0);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        return canvas.getCanvasElement();
    }

}
