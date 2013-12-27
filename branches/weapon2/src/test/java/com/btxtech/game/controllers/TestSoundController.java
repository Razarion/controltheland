package com.btxtech.game.controllers;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * User: beat
 * Date: 21.03.2012
 * Time: 14:11:49
 */
public class TestSoundController extends AbstractServiceTest {
    @Autowired
    private SoundService soundService;
    @Autowired
    private SoundController soundController;

    @Test
    public void simple() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSound sound1 = soundService.getSoundLibraryCrud().createDbChild();
        sound1.setName("sound1");
        sound1.setDataMp3(new byte[]{0, 1, 2, 3});
        sound1.setDataOgg(new byte[]{4, 5, 6, 7});
        soundService.getSoundLibraryCrud().updateDbChild(sound1);
        int soundId1 = sound1.getId();
        DbSound sound2 = soundService.getSoundLibraryCrud().createDbChild();
        sound2.setName("sound2");
        sound2.setDataMp3(new byte[]{1, 2, 3, 43});
        sound2.setDataOgg(new byte[]{5, 6, 7, 8});
        soundService.getSoundLibraryCrud().updateDbChild(sound2);
        int soundId2 = sound2.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        soundController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());

        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("id", Integer.toString(soundId1));
        mockHttpServletRequest.setParameter("cdc", "audio/mpeg");
        mockHttpServletResponse = new MockHttpServletResponse();
        soundController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertNull(mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("audio/mpeg", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3}, mockHttpServletResponse.getContentAsByteArray());

        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("id", Integer.toString(soundId1));
        mockHttpServletRequest.setParameter("cdc", "audio/ogg");
        mockHttpServletResponse = new MockHttpServletResponse();
        soundController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertNull(mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("audio/ogg", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{4, 5, 6, 7}, mockHttpServletResponse.getContentAsByteArray());

        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("id", Integer.toString(soundId2));
        mockHttpServletRequest.setParameter("cdc", "audio/mpeg");
        mockHttpServletResponse = new MockHttpServletResponse();
        soundController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertNull(mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("audio/mpeg", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 43}, mockHttpServletResponse.getContentAsByteArray());

        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("id", Integer.toString(soundId2));
        mockHttpServletRequest.setParameter("cdc", "audio/ogg");
        mockHttpServletResponse = new MockHttpServletResponse();
        soundController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertNull(mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("audio/ogg", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{5, 6, 7, 8}, mockHttpServletResponse.getContentAsByteArray());
    }
}
