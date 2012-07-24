package com.btxtech.game.services.socialnet.facebook;

import com.btxtech.game.services.socialnet.SocialUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.ArrayUtils;
import org.apache.wicket.util.crypt.Base64;

import java.io.UnsupportedEncodingException;

/**
 * User: beat
 * Date: 22.07.12
 * Time: 15:57
 */
public class FacebookUtil {
    public static String makeUrlSafe(String input) {
        String correctedInput = input.replace('-', '+');
        correctedInput = correctedInput.replace('_', '/');
        String padding = "";
        switch (correctedInput.length() % 4) {
        case 0:
            break;
        case 1:
            padding = "===";
            break;
        case 2:
            padding = "==";
            break;
        default:
            padding = "=";
        }
        return correctedInput + padding;
    }

    public static byte[] enhancedBase64UrlSafeDecode(String input) {
        try {
            return Base64.decodeBase64(makeUrlSafe(input).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] splitSignedRequest(String signedRequestParameter) {
        if (signedRequestParameter == null || signedRequestParameter.isEmpty()) {
            throw new IllegalArgumentException("Empty signed_request received");
        }
        String[] paramParts = signedRequestParameter.split("\\.");
        if (paramParts.length != 2) {
            throw new IllegalArgumentException("Invalid signed request parameter received. Exactly one '.' expected. Received: " + (paramParts.length - 1) + " signedRequestParameter: " + signedRequestParameter);
        }
        return paramParts;
    }

    public static FacebookSignedRequest getFacebookSignedRequest(String playLoad) {
        byte[] payloadBytes = enhancedBase64UrlSafeDecode(playLoad);
        Gson gson = new Gson();
        return gson.fromJson(new String(payloadBytes), FacebookSignedRequest.class);
    }

    public static void checkSignature(String secret, String payload, String base64UrlSafeSignature) {
        //Utils.printJarLocation(Base64.class);
        //   byte[] signature2 = new Base64(true).decode(base64UrlSafeSignature);
        byte[] signature = enhancedBase64UrlSafeDecode(base64UrlSafeSignature);
        byte[] calculatedSignature = SocialUtil.getHmacSha256Hash(secret, payload.getBytes());
        if (!ArrayUtils.isEquals(signature, calculatedSignature)) {
            throw new IllegalArgumentException("Signature does not match");
        }
    }
}
