package com.btxtech.game.services.common;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;

import java.io.StringWriter;
import java.util.Map;

/**
 * User: beat
 * Date: 01.02.13
 * Time: 14:07
 */
public class ExtendedVelocityEngineUtils {
    /**
     * Merge the specified Velocity template with the given model and write
     * the result to the given Writer.
     *
     * @param velocityEngine VelocityEngine to work with
     * @param inString       input string containing the VTL to be rendered
     * @param model          the Map that contains model names as keys and model objects
     *                       as values
     * @return result
     * @throws org.apache.velocity.exception.VelocityException
     *          if the template wasn't found or rendering failed
     */
    public static String evaluate(VelocityEngine velocityEngine, String inString, Map model) throws VelocityException {
        VelocityContext velocityContext = new VelocityContext(model);
        StringWriter result = new StringWriter();
        velocityEngine.evaluate(velocityContext, result, "", inString);
        return result.toString();
    }

}
