package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.services.TestPlanetHelper;
import com.btxtech.game.services.common.TestGlobalServices;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.impl.BaseServiceImpl;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 19.11.12
 * Time: 22:05
 */
public class TestConditionServiceTrigger {

    @Test
    public void testMoney() {
        TestPlanetHelper testPlanetHelper = new TestPlanetHelper();
        ServerPlanetServicesImpl serverPlanetServices = new ServerPlanetServicesImpl();
        PlanetInfo planetInfo = new PlanetInfo();
        planetInfo.setPlanetIdAndName(1, "TestPlanet", null);
        planetInfo.setMaxMoney(10);
        serverPlanetServices.setPlanetInfo(planetInfo);
        testPlanetHelper.setServerPlanetServices(serverPlanetServices);

        Base testBase = new Base(testPlanetHelper, 1);

        ServerConditionService serverConditionServiceMock = EasyMock.createStrictMock(ServerConditionService.class);
        serverConditionServiceMock.onMoneyIncrease(testBase.getSimpleBase(), 1.0);
        serverConditionServiceMock.onMoneyIncrease(testBase.getSimpleBase(), 8.0);
        serverConditionServiceMock.onMoneyIncrease(testBase.getSimpleBase(), 7.0);
        serverConditionServiceMock.onMoneyIncrease(testBase.getSimpleBase(), 5.0);
        EasyMock.replay(serverConditionServiceMock);

        GuildService guildServiceMock = EasyMock.createNiceMock(GuildService.class);
        EasyMock.replay(guildServiceMock);

        TestGlobalServices testGlobalServices = new TestGlobalServices();
        testGlobalServices.setServerConditionService(serverConditionServiceMock);
        testGlobalServices.setGuildService(guildServiceMock);

        BaseServiceImpl baseService = new BaseServiceImpl(testPlanetHelper);
        baseService.init(serverPlanetServices, testGlobalServices);
        baseService.restore(Collections.singletonList(testBase));

        // Start Test
        Assert.assertEquals(0.0, testBase.getAccountBalance());
        baseService.depositResource(1.0, testBase.getSimpleBase());
        Assert.assertEquals(1.0, testBase.getAccountBalance());
        baseService.depositResource(8.0, testBase.getSimpleBase());
        Assert.assertEquals(9.0, testBase.getAccountBalance());
        baseService.depositResource(7.0, testBase.getSimpleBase());
        Assert.assertEquals(10.0, testBase.getAccountBalance());
        baseService.depositResource(5.0, testBase.getSimpleBase());
        Assert.assertEquals(10.0, testBase.getAccountBalance());
        EasyMock.verify(serverConditionServiceMock);
    }

}
