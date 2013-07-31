package com.btxtech.game.services.socialnet.facebook;

import com.btxtech.game.services.socialnet.SocialUtil;
import junit.framework.Assert;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

/**
 * User: beat
 * Date: 22.07.12
 * Time: 16:02
 */
public class TestFacebookUtil {

    @Test
    public void enhancedBase64UrlSafeDecode() {
        Assert.assertEquals("ab+cd===", FacebookUtil.makeUrlSafe("ab-cd"));
        Assert.assertEquals("ab/cd===", FacebookUtil.makeUrlSafe("ab_cd"));
        Assert.assertEquals("a/b/+/c/d===", FacebookUtil.makeUrlSafe("a_b_-_c_d"));
        Assert.assertEquals("abcd", FacebookUtil.makeUrlSafe("abcd"));
    }

    @Test
    public void splitSignedRequest() {
        String[] paramParts = FacebookUtil.splitSignedRequest("1234.abcde");
        Assert.assertEquals(2, paramParts.length);
        Assert.assertEquals("1234", paramParts[0]);
        Assert.assertEquals("abcde", paramParts[1]);

        try {
            FacebookUtil.splitSignedRequest(null);
            Assert.fail("FacebookUrlException expected");
        } catch (FacebookUrlException e) {
            // Expected
        }

        try {
            FacebookUtil.splitSignedRequest("");
            Assert.fail("FacebookUrlException expected");
        } catch (FacebookUrlException e) {
            // Expected
        }

        try {
            FacebookUtil.splitSignedRequest("sdjfsdf");
            Assert.fail("FacebookUrlException expected");
        } catch (FacebookUrlException e) {
            // Expected
        }

        try {
            FacebookUtil.splitSignedRequest("sd.jfs.df");
            Assert.fail("FacebookUrlException expected");
        } catch (FacebookUrlException e) {
            // Expected
        }
    }

    @Test
    public void getFacebookSignedRequestRegisteredUser() {
        FacebookSignedRequest facebookSignedRequest = FacebookUtil.getFacebookSignedRequest("eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0Mjk2NjU0NCwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        Assert.assertEquals("HMAC-SHA256", facebookSignedRequest.getAlgorithm());
        Assert.assertEquals(1342966544, facebookSignedRequest.getIssuedAt());
        Assert.assertEquals("ch", facebookSignedRequest.getUser().getCountry());
        Assert.assertEquals("en_US", facebookSignedRequest.getUser().getLocale());
        Assert.assertEquals(21, facebookSignedRequest.getUser().getAge().getMin());
        Assert.assertNull(facebookSignedRequest.getUserId());
        Assert.assertNull(facebookSignedRequest.getOAuthToken());
        Assert.assertFalse(facebookSignedRequest.hasUserId());
        Assert.assertFalse(facebookSignedRequest.hasOAuthToken());
    }

    @Test
    public void getFacebookSignedRequestNewUser() {
        FacebookSignedRequest facebookSignedRequest = FacebookUtil.getFacebookSignedRequest("eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMwMDE2MDAsImlzc3VlZF9hdCI6MTM0Mjk5NDcyNywib2F1dGhfdG9rZW4iOiJBQUFDeHFBNlVob2tCQUJLSW9Edjd0SGlqMzZjQXFrTnV6Ym9BQzVGM1ZCMFpCcE5aQnBJeU5GcWllcDhuR0RMS1ZCR3dMVDByWEh6azgxZ2tuTWNFd053aURIajQybFZnb0tqMklxcjZkNFZKRlh5OElCIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        Assert.assertEquals("HMAC-SHA256", facebookSignedRequest.getAlgorithm());
        Assert.assertEquals(1342994727, facebookSignedRequest.getIssuedAt());
        Assert.assertEquals("ch", facebookSignedRequest.getUser().getCountry());
        Assert.assertEquals("en_US", facebookSignedRequest.getUser().getLocale());
        Assert.assertEquals(21, facebookSignedRequest.getUser().getAge().getMin());
        Assert.assertEquals("100003634094139", facebookSignedRequest.getUserId());
        Assert.assertEquals("AAACxqA6UhokBABKIoDv7tHij36cAqkNuzboAC5F3VB0ZBpNZBpIyNFqiep8nGDLKVBGwLT0rXHzk81gknMcEwNwiDHj42lVgoKj2Iqr6d4VJFXy8IB", facebookSignedRequest.getOAuthToken());
        Assert.assertTrue(facebookSignedRequest.hasUserId());
        Assert.assertTrue(facebookSignedRequest.hasOAuthToken());
    }

    @Test
    public void getHmacSha256Hash() {
        final byte[] DIGEST = {-66, 85, -32, -69, -82, 1, 64, 97, 82, 66, -74, 52, 101, -62, 65, 100, 4, -116, -67, -117, -45, 30, -17, 70, 67, 70, 12, -10, -72, -49, 74, -54};
        final byte[] PAYLOAD = {101, 121, 74, 104, 98, 71, 100, 118, 99, 109, 108, 48, 97, 71, 48, 105, 79, 105, 74, 73, 84, 85, 70, 68, 76, 86, 78, 73, 81, 84, 73, 49, 78, 105, 73, 115, 73, 106, 65, 105, 79, 105, 74, 119, 89, 88, 108, 115, 98, 50, 70, 107, 73, 110, 48};
        byte[] calculatedDigest = SocialUtil.getHmacSha256Hash("secret", PAYLOAD);
        Assert.assertTrue(ArrayUtils.isEquals(DIGEST, calculatedDigest));

        //   System.out.println("'" + new String(Base64UrlSafe.decodeBase64("vlXgu64BQGFSQrY0ZcJBZASMvYvTHu9GQ0YM9rjPSso".getBytes())) + "'");
        //   System.out.println("'" + new String(SocialUtil.getHmacSha256Hash("secret", "eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsIjAiOiJwYXlsb2FkIn0".getBytes())) + "'");
    }

    @Test
    public void checkSignature() {
        FacebookUtil.checkSignature("secret", "eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsIjAiOiJwYXlsb2FkIn0", "vlXgu64BQGFSQrY0ZcJBZASMvYvTHu9GQ0YM9rjPSso");
        try {
            FacebookUtil.checkSignature("secret1", "eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsIjAiOiJwYXlsb2FkIn0", "vlXgu64BQGFSQrY0ZcJBZASMvYvTHu9GQ0YM9rjPSso");
            Assert.fail("FacebookUrlException expected");
        } catch (FacebookUrlException e) {
            // Expected
        }
        try {
            FacebookUtil.checkSignature("secret", "XXeyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsIjAiOiJwYXlsb2FkIn0", "vlXgu64BQGFSQrY0ZcJBZASMvYvTHu9GQ0YM9rjPSso");
            Assert.fail("FacebookUrlException expected");
        } catch (FacebookUrlException e) {
            // Expected
        }
        try {
            FacebookUtil.checkSignature("secret", "eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsIjAiOiJwYXlsb2FkIn0", "vlXgu64BQGFSQrY0ZcJBZASMvYvTHu9GQ0YM9rjPSqw");
            Assert.fail("FacebookUrlException expected");
        } catch (FacebookUrlException e) {
            // Expected
        }
        FacebookUtil.checkSignature("efff51df6827177b030e01fdb402f697",
                "eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0Mjk3MDk4NCwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19",
                "sLZ6D74IdMvvR1jqYtH_TnWJP3l_Ne5_CXaiPlD5TvM");
    }

}
