package com.btxtech.game.jsre.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 03.01.13
 * Time: 14:51
 */
public interface ClientI18nConstants extends Messages {
    // Common
    String userName();

    String email();

    String confirmEmail();

    String password();

    String confirmPassword();

    String skip();

    String close();

    String tooltipCloseDialog();

    String tooltipMinimize();

    String tooltipMaximize();

    String reject();

    String accept();

    String highScore();

    String limitation();

    String houseSpace();

    String tooManyItems();

    String spaceLimitExceeded();

    String yes();

    String no();

    String newBase();

    String startOver();

    String stop();

    String mission();

    String refresh();

    String player();

    String planet();

    String items();

    String money();

    String filter();

    String activate();

    String cancel();

    String start();

    String description();

    String abort();

    // Radar
    String tooltipRadar();

    String tooltipZoomIn();

    String tooltipZoomOut();

    String tooltipZoomHome();

    String tooltipZoomAttack();

    String tooltipRadarPageLeft();

    String tooltipRadarPageRight();

    String tooltipRadarPageUp();

    String tooltipRadarPageDown();

    String radarNoPower();

    String radarNoRadarBuilding();

    // Dialogs
    String message();

    String inventory();

    String inventoryNotAvailable();

    // Connection
    String connectionFailed();

    String connectionLost();

    String connectionAnotherExits();

    String connectionNone();

    String wrongBase();

    String notYourBase();

    // Sell
    String sell();

    String sellNotAvailable();

    // Register dialog
    String registerText();

    String registerThanks();

    String registerThanksLong();

    String registerConfirmationEmailSent(String email);

    String register();

    String registerDirect();

    String registerFacebook();

    String registrationFailed();

    String registrationFilled();

    String registrationMatch();

    String registrationEmailNotValid();

    String registrationEmailMatch();

    String registrationEmail(String email);

    String registrationUser();

    String chooseNickName();

    // Side Cockpit
    String tooltipMoney();

    String tooltipUnitsAmount();

    String tooltipEnergy();

    String tooltipAlliances();

    String tooltipSell();

    String tooltipMute();

    String tooltipHighScore();

    String tooltipInventory();

    String tooltipFacebookCommunity();

    String tooltipFacebookInvite();

    String singlePlayer();

    String tooltipAbortMission();

    String multiplayer();

    String tooltipLevel();

    String level();

    String tooltipXp();

    String xp();

    // Alliance dialog
    String alliances();

    String alliancesAll();

    String alliancesAllNotAvailable();

    String alliancesNo();

    String alliancesOffer();

    String alliancesOffered(String userName);

    String breakAlliance();

    // Quests
    String quests();

    String nextPlanet();

    String tooltipNextPlanet();

    String questDialog();

    String tooltipQuestDialog();

    String questVisualisation();

    String tooltipQuestVisualisation();

    String pressButtonWhenReady();

    String tooltipStartMission();

    String reward(int gold, int xp);

    String activeQuest();

    String activeQuestAbort();

    String questOverview(int questsDone, int totalQuests);

    String missionOverview(int missionsDone, int totalMissions);

    String noActiveQuest();

    String questBasesKilled();

    String questOilCollected();

    String questBuilt();

    String questUnitStructuresBuilt();

    String questDestroyed();

    String questUnitStructuresDestroyed();

    String questMinutesPast();

    String startMission();

    String competeMission();

    String go();

    String questEnumPvp();

    String questEnumPve();

    String questEnumBossPve();

    String questEnumMission();

    String questEnumGather();

    String questEnumMoney();

    String questEnumBuildup();

    String questEnumHoldTheBase();

    String questType();

    String abortMission();

    String reallyAbortMission();

    // No money dialog
    String noMoney();

    String notEnoughMoney();

    String noMoneyNewBase();

    String startNewBase();

    // Item cockpit
    String offerAlliance();

    String tooltipUnloadContainer();

    String tooltipLaunchMissile();

    String tooltipUpgrade();

    String tooltipBuild(String itemName);

    String tooltipNoBuildLevel(String itemName);

    String tooltipNoBuildLimit(String itemName);

    String tooltipNoBuildHouseSpace(String itemName);

    String tooltipNoBuildMoney(String itemName);

    String botEnemy();

    String playerEnemy();

    String allianceMember();

    String tooltipSelect(String itemName);

    String notPlaceHere();

    String notPlaceOver();

    // Highscore
    String findMe();

    String rank();

    String killed();

    String killedPve();

    String killedPvp();

    String basesKilled();

    String basesLost();

    String created();

    // Inventory
    String useItem();

    String workshop();

    String dealer();

    String funds();

    String artifacts();

    String buy();

    String tooltipAssemble();

    String assemble();

    String tooltipArtifact(String artifactName);

    String buyRazarionPaypal();

    String buyRazarionPaypal1000();

    String buyRazarionPaypal2200();

    String buyRazarionPaypal4600();

    String buyRazarionPaypal12500();

    String buyRazarionPaypalOnlyRegistered();

    String youOwn(int ownCount);

    String useItemLimit(String itemName);

    String useItemHouseSpace();

    String useItemMoney();

    String enemyTooNear();

    String razarionAmount(int razarion);

    String filterAll();

    String filterCurrent();

    String cost(int razarionCost);

    String leaveBaseNextPlanet();

    // Startup
    String startupClearGame();

    String startupLoadRealGameInfo();

    String startupDeltaStartRealGame();

    String startupLoadUnits();

    String startupInitUnits();

    String startupRunRealGame();

    String startupLoadJavaScript();

    String startupInitGui();

    String startupInitRealGame();

    String startupPreloadImageSpriteMaps();

    String startupLoadSimulationGameInfo();

    String startupInitSimulatedGame();

    String startupRunSimulatedGame();

    String startupDeltaStartSimulatedGame();

    // Unlock dialogs
    String unlockItemDialogTitle();

    String unlockQuestDialogTitle();

    String unlockPlanetDialogTitle();

    String itemDialogNoRazarionMessage(String itemTypeName, Integer unlockRazarion, Integer razarion);

    String questDialogNoRazarionMessage(String questName, Integer unlockRazarion, Integer razarion);

    String planetDialogNoRazarionMessage(String planetName, Integer unlockRazarion, Integer razarion);

    String itemIsLocked(String itemTypeName);

    String questIsLocked(String title);

    String planetIsLocked(String planetName);

    String unlockRazarionCost(int unlockRazarion);

    String availableRazarion(int razarion);

    String unlockButton();

    String questLocked();

    String planetLocked();

    String serverRebootTitle();

    String serverRebootMessage(int rebootInSeconds, int downTimeInMinutes);

    String serverRebootMissionNotSaved();

    String serverRebootNotRegistered();
}
