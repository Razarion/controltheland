package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.impl.ServerGlobalConnectionServiceImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 04.03.13
 * Time: 17:21
 */
public class TestServerGlobalConnectionService extends AbstractServiceTest {
    @Autowired
    private ServerGlobalConnectionService connection;

    @Test
    @DirtiesContext
    public void getAllOnlineMissionUserState() throws Exception {
        configureSimplePlanetNoResources();

        setPrivateStaticField(ServerGlobalConnectionServiceImpl.class, "ONLINE_MISSION_TIMER_DELAY", 500);
        ServerGlobalConnectionServiceImpl connectionImpl = (ServerGlobalConnectionServiceImpl) deAopProxy(connection);
        connectionImpl.destroy();
        connectionImpl.init();

        Thread.sleep(510);
        Assert.assertEquals(0, connection.getAllOnlineMissionUserState().size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(510);
        // TODO failed on 14.03.2013 05.07.2013
        Assert.assertEquals(1, connection.getAllOnlineMissionUserState().size());
    }

    @Test
    @DirtiesContext
    public void getAllOnlineMissionUserStateSameMultiple() throws Exception {
        configureSimplePlanetNoResources();

        setPrivateStaticField(ServerGlobalConnectionServiceImpl.class, "ONLINE_MISSION_TIMER_DELAY", 500);
        ServerGlobalConnectionServiceImpl connectionImpl = (ServerGlobalConnectionServiceImpl) deAopProxy(connection);
        connectionImpl.destroy();
        connectionImpl.init();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(510);
        Assert.assertEquals(1, connection.getAllOnlineMissionUserState().size());
    }

    @Test
    @DirtiesContext
    public void getAllOnlineMissionUserStateMultiple() throws Exception {
        configureSimplePlanetNoResources();

        setPrivateStaticField(ServerGlobalConnectionServiceImpl.class, "ONLINE_MISSION_TIMER_DELAY", 500);
        ServerGlobalConnectionServiceImpl connectionImpl = (ServerGlobalConnectionServiceImpl) deAopProxy(connection);
        connectionImpl.destroy();
        connectionImpl.init();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, GameEngineMode.MASTER);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        connection.pollMessageIdPackets(null, ChatMessageFilter.GUILD, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GUILD, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GUILD, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GUILD, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GUILD, GameEngineMode.MASTER);
        connection.pollMessageIdPackets(null, ChatMessageFilter.GUILD, GameEngineMode.MASTER);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(510);
        Assert.assertEquals(3, connection.getAllOnlineMissionUserState().size());
    }
}
