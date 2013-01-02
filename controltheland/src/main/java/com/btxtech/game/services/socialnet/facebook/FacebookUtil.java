package com.btxtech.game.services.socialnet.facebook;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.socialnet.SocialUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.ArrayUtils;
import org.apache.wicket.util.crypt.Base64;
import org.apache.wicket.util.io.IOUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

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

    public static FacebookSignedRequest createAndCheckFacebookSignedRequest(String facebookAppSecret, String signedRequestParameter) {
        String[] signedRequestParts = FacebookUtil.splitSignedRequest(signedRequestParameter);

        FacebookSignedRequest facebookSignedRequest = FacebookUtil.getFacebookSignedRequest(signedRequestParts[1]);
        if (!facebookSignedRequest.getAlgorithm().toUpperCase().equals("HMAC-SHA256")) {
            throw new IllegalArgumentException("Invalid signature algorithm received: " + facebookSignedRequest.getAlgorithm());
        }

        FacebookUtil.checkSignature(facebookAppSecret, signedRequestParts[1], signedRequestParts[0]);
        return facebookSignedRequest;
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

    public static String doGraphApiCall4Email(String facebookUserId, String accessToken) throws IOException, URISyntaxException {
        ClientHttpResponse clientHttpResponse = null;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        try {
            if (facebookUserId == null) {
                throw new IllegalArgumentException("facebookUserId == null");
            }
            if (accessToken == null) {
                throw new IllegalArgumentException("accessToken == null");
            }
            StringBuilder builder = new StringBuilder();
            builder.append("https://graph.facebook.com");
            builder.append("/");
            builder.append(facebookUserId);
            builder.append("?access_token=");
            builder.append(accessToken);
            builder.append("&fields=name,email");
            ClientHttpRequest clientHttpRequest = factory.createRequest(new URI(builder.toString()), HttpMethod.GET);
            clientHttpResponse = clientHttpRequest.execute();
            if (clientHttpResponse.getStatusCode() == HttpStatus.OK) {
                Gson gson = new Gson();
                FacebookUserDetails facebookUserDetails = gson.fromJson(IOUtils.toString(clientHttpResponse.getBody()), FacebookUserDetails.class);
                return facebookUserDetails.getEmail();
            } else {
                throw new IllegalStateException("Facebook graph call fails: " + clientHttpResponse.getStatusText() + " " + clientHttpResponse.getStatusCode() + "|" + builder.toString());
            }
        } finally {
            if (clientHttpResponse != null) {
                clientHttpResponse.close();
            }
        }
    }
}
