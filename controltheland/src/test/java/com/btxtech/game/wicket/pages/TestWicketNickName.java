package com.btxtech.game.wicket.pages;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.forum.ForumService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.socialnet.facebook.FacebookAge;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUser;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Locale;

/**
 * User: beat
 * Date: 13.07.13
 * Time: 09:53
 */
public class TestWicketNickName extends AbstractServiceTest {
    @Test
    @DirtiesContext
    public void validNickName() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookAge facebookAge = new FacebookAge(20);
        FacebookUser facebookUser = new FacebookUser("", "", facebookAge);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest("", 0, facebookUser, "", "1234");
        facebookSignedRequest.setEmail("muu@ggg.com");
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        // Enter valid nickname
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "abced");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(Game.class);
        Page gamePage = getWicketTester().getLastRenderedPage();
        Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, gamePage.getPageParameters().get("taskId").toInt());
        User user = getUser();
        Assert.assertEquals(User.SocialNet.FACEBOOK, user.getSocialNet());
        Assert.assertEquals("abced", user.getUsername());
        Assert.assertEquals("1234", user.getSocialNetUserId());
        Assert.assertEquals("muu@ggg.com", user.getEmail());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void invalidNickNameAndSendEn() throws Exception {
        configureMultiplePlanetsAndLevels();

        FacebookSignedRequest facebookSignedRequest = createUsersAndRequest();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        // Enter No nickname
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("form:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        // Enter too short nickname
        formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "x");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("form:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        // Enter existing nickname
        formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "qayxsw");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("form:feedback:feedbackul:messages:0:message", "Name has already been taken");
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void invalidNickNameAndSendDe() throws Exception {
        configureMultiplePlanetsAndLevels();

        FacebookSignedRequest facebookSignedRequest = createUsersAndRequest();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        // Enter No nickname
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("form:feedback:feedbackul:messages:0:message", "Name muss mindestens 3 Zeichen lang sein");
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void invalidNickNameAndSendCN() throws Exception {
        configureMultiplePlanetsAndLevels();

        FacebookSignedRequest facebookSignedRequest = createUsersAndRequest();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        // Enter No nickname
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("form:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void nickNameAjaxEn() throws Exception {
        configureMultiplePlanetsAndLevels();

        FacebookSignedRequest facebookSignedRequest = createUsersAndRequest();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        // Enter too short nickname
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "x");
        getWicketTester().executeAjaxEvent("form:name", "onkeyup");
        getWicketTester().assertLabel("form:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        // Enter existing nickname
        formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "qayxsw");
        getWicketTester().executeAjaxEvent("form:name", "onkeyup");
        getWicketTester().assertLabel("form:feedback:feedbackul:messages:0:message", "Name has already been taken");
        Assert.assertNull(getUser());
        // Valid nick name
        formTester = getWicketTester().newFormTester("form");
        formTester.setValue("name", "hallo");
        getWicketTester().executeAjaxEvent("form:name", "onkeyup");
        getWicketTester().assertNoErrorMessage();
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTODO() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(FacebookAppStart.class);
        Assert.fail("...TODO..., tracking, etc");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private FacebookSignedRequest createUsersAndRequest() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("qayxsw", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        FacebookAge facebookAge = new FacebookAge(20);
        FacebookUser facebookUser = new FacebookUser("", "", facebookAge);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest("", 0, facebookUser, "", "1234");
        facebookSignedRequest.setEmail("muu@ggg.com");
        return facebookSignedRequest;
    }

}
