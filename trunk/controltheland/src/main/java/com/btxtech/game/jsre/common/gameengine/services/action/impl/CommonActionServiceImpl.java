package com.btxtech.game.jsre.common.gameengine.services.action.impl;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderFinalizeCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LaunchCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LoadContainCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UpgradeCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 07.10.2011
 * Time: 10:56:06
 */
public abstract class CommonActionServiceImpl implements CommonActionService {
    private static Logger log = Logger.getLogger(CommonActionServiceImpl.class.getName());
    private final HashSet<SyncBaseItem> guardingItems = new HashSet<SyncBaseItem>();

    protected abstract void executeCommand(SyncBaseItem syncItem, BaseCommand baseCommand) throws ItemLimitExceededException, HouseSpaceExceededException, ItemDoesNotExistException, NoSuchItemTypeException, InsufficientFundsException, NotYourBaseException;

    protected abstract Services getServices();

    protected boolean checkCommand(SyncItem syncItem) {
        Id id = syncItem.getId();
        if (id == null) {
            log.severe("Can not send command: id is null");
            return true;
        }
        if (!id.isSynchronized()) {
            log.severe("Can not execute command: Id is not synchronized: ");
            return true;
        }
        return false;
    }

    @Override
    public void move(SyncBaseItem syncBaseItem, Index destination) {
        syncBaseItem.stop();
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(syncBaseItem.getId());
        moveCommand.setTimeStamp();
        List<Index> pathToDestination = getServices().getCollisionService().setupPathToDestination(syncBaseItem, destination);
        moveCommand.setPathToDestination(pathToDestination);
        if (pathToDestination.size() < 2) {
            moveCommand.setDestinationAngel(syncBaseItem.getSyncItemArea().getTurnToAngel(destination));
        } else {
            int size = pathToDestination.size();
            moveCommand.setDestinationAngel(pathToDestination.get(size - 2).getAngleToNord(pathToDestination.get(size - 1)));
        }
        try {
            executeCommand(syncBaseItem, moveCommand);
        } catch (PathCanNotBeFoundException e) {
            log.warning("PathCanNotBeFoundException: " + e.getMessage());
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void attack(SyncBaseItem syncBaseItem, SyncBaseItem target, Index destinationHint, double destinationAngel, boolean followTarget) {
        if (checkCommand(syncBaseItem)) {
            return;
        }
        syncBaseItem.stop();
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(syncBaseItem.getId());
        attackCommand.setTimeStamp();
        attackCommand.setTarget(target.getId());
        attackCommand.setFollowTarget(followTarget);
        if (followTarget) {
            attackCommand.setPathToDestination(getServices().getCollisionService().setupPathToDestination(syncBaseItem, destinationHint));
            attackCommand.setDestinationAngel(destinationAngel);
        }

        try {
            executeCommand(syncBaseItem, attackCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void defend(SyncBaseItem attacker, SyncBaseItem target) {
        attack(attacker, target, null, 0, false);
    }

    @Override
    public void build(SyncBaseItem syncItem, Index positionToBeBuild, BaseItemType toBeBuilt) {
        if (checkCommand(syncItem)) {
            return;
        }
        syncItem.stop();
        BuilderCommand builderCommand = new BuilderCommand();
        builderCommand.setId(syncItem.getId());
        builderCommand.setTimeStamp();
        builderCommand.setToBeBuilt(toBeBuilt.getId());
        builderCommand.setPositionToBeBuilt(positionToBeBuild);
        AttackFormationItem format = getServices().getCollisionService().getDestinationHint(syncItem,
                syncItem.getSyncBuilder().getBuilderType().getRange(),
                toBeBuilt.getBoundingBox().createSyntheticSyncItemArea(positionToBeBuild),
                toBeBuilt.getTerrainType());
        if (format.isInRange()) {
            builderCommand.setPathToDestination(getServices().getCollisionService().setupPathToDestination(syncItem, format.getDestinationHint()));
            builderCommand.setDestinationAngel(format.getDestinationAngel());
        } else {
            move(syncItem, format.getDestinationHint());
            return;
        }

        try {
            executeCommand(syncItem, builderCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void build(SyncBaseItem syncItem, Index positionToBeBuild, BaseItemType toBeBuilt, Index destinationHint, double destinationAngel) {
        if (checkCommand(syncItem)) {
            return;
        }
        syncItem.stop();
        BuilderCommand builderCommand = new BuilderCommand();
        builderCommand.setId(syncItem.getId());
        builderCommand.setTimeStamp();
        builderCommand.setToBeBuilt(toBeBuilt.getId());
        builderCommand.setPositionToBeBuilt(positionToBeBuild);
        builderCommand.setPathToDestination(getServices().getCollisionService().setupPathToDestination(syncItem, destinationHint));
        builderCommand.setDestinationAngel(destinationAngel);

        try {
            executeCommand(syncItem, builderCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void finalizeBuild(SyncBaseItem builder, SyncBaseItem building, Index destinationHint, double destinationAngel) {
        if (checkCommand(builder)) {
            return;
        }
        if (checkCommand(building)) {
            return;
        }
        builder.stop();
        BuilderFinalizeCommand builderFinalizeCommand = new BuilderFinalizeCommand();
        builderFinalizeCommand.setId(builder.getId());
        builderFinalizeCommand.setTimeStamp();
        builderFinalizeCommand.setToBeBuilt(building.getId());
        builderFinalizeCommand.setPathToDestination(getServices().getCollisionService().setupPathToDestination(builder, destinationHint));
        builderFinalizeCommand.setDestinationAngel(destinationAngel);
        try {
            executeCommand(builder, builderFinalizeCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void fabricate(SyncBaseItem factory, BaseItemType itemType) {
        if (checkCommand(factory) || !factory.isReady() || factory.getSyncFactory().isActive()) {
            return;
        }
        FactoryCommand factoryCommand = new FactoryCommand();
        factoryCommand.setId(factory.getId());
        factoryCommand.setTimeStamp();
        factoryCommand.setToBeBuilt(itemType.getId());

        try {
            executeCommand(factory, factoryCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void collect(SyncBaseItem collector, SyncResourceItem money, Index destinationHint, double destinationAngel) {
        if (checkCommand(collector)) {
            return;
        }
        collector.stop();
        MoneyCollectCommand collectCommand = new MoneyCollectCommand();
        collectCommand.setId(collector.getId());
        collectCommand.setTimeStamp();
        collectCommand.setTarget(money.getId());
        collectCommand.setPathToDestination(getServices().getCollisionService().setupPathToDestination(collector, destinationHint));
        collectCommand.setDestinationAngel(destinationAngel);

        try {
            executeCommand(collector, collectCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void upgrade(SyncBaseItem item) {
        if (checkCommand(item)) {
            return;
        }
        item.stop();
        UpgradeCommand upgradeCommand = new UpgradeCommand();
        upgradeCommand.setId(item.getId());
        upgradeCommand.setTimeStamp();
        try {
            executeCommand(item, upgradeCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void loadContainer(SyncBaseItem container, SyncBaseItem item) {
        if (checkCommand(item)) {
            return;
        }
        if (checkCommand(container)) {
            return;
        }

        container.stop();
        LoadContainCommand loadContainCommand = new LoadContainCommand();
        loadContainCommand.setId(item.getId());
        loadContainCommand.setTimeStamp();
        loadContainCommand.setItemContainer(container.getId());

        try {
            executeCommand(item, loadContainCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void unloadContainer(SyncBaseItem container, Index unloadPos) {
        if (checkCommand(container)) {
            return;
        }

        UnloadContainerCommand unloadContainerCommand = new UnloadContainerCommand();
        unloadContainerCommand.setId(container.getId());
        unloadContainerCommand.setTimeStamp();
        unloadContainerCommand.setUnloadPos(unloadPos);

        try {
            executeCommand(container, unloadContainerCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public void launch(SyncBaseItem launcherItem, Index target) {
        if (checkCommand(launcherItem)) {
            return;
        }

        LaunchCommand launchCommand = new LaunchCommand();
        launchCommand.setId(launcherItem.getId());
        launchCommand.setTimeStamp();
        launchCommand.setTarget(target);

        try {
            executeCommand(launcherItem, launchCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void addGuardingBaseItem(SyncTickItem syncTickItem) {
        if (getServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
            return;
        }

        if (!(syncTickItem instanceof SyncBaseItem)) {
            return;
        }

        SyncBaseItem syncBaseItem = (SyncBaseItem) syncTickItem;
        if (!syncBaseItem.hasSyncWeapon() || !syncBaseItem.isAlive()) {
            return;
        }

        if (syncBaseItem.hasSyncConsumer() && !syncBaseItem.getSyncConsumer().isOperating()) {
            return;
        }

        if (!syncBaseItem.getSyncItemArea().hasPosition()) {
            return;
        }

        if (!getServices().getTerritoryService().isAllowed(syncBaseItem.getSyncItemArea().getPosition(), syncBaseItem)) {
            return;
        }

        if (checkGuardingItemHasEnemiesInRange(syncBaseItem)) {
            return;
        }

        synchronized (guardingItems) {
            guardingItems.add(syncBaseItem);
        }
    }

    @Override
    public void removeGuardingBaseItem(SyncBaseItem syncItem) {
        if (getServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
            return;
        }
        if (!syncItem.hasSyncWeapon()) {
            return;
        }

        synchronized (guardingItems) {
            guardingItems.remove(syncItem);
        }
    }

    protected void clearGuardingBaseItem() {
        synchronized (guardingItems) {
            guardingItems.clear();
        }
    }

    @Override
    public void interactionGuardingItems(SyncBaseItem target) {
        if (getServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
            return;
        }
        if (target.isContainedIn()) {
            return;
        }
        boolean isTargetBot = getServices().getBaseService().isBot(target.getBase());
        // Prevent ConcurrentModificationException
        List<SyncBaseItem> attackers = new ArrayList<SyncBaseItem>();
        synchronized (guardingItems) {
            for (SyncBaseItem attacker : guardingItems) {
                if (attacker.isEnemy(target)
                        && attacker.getSyncWeapon().isAttackAllowedWithoutMoving(target)
                        && attacker.getSyncWeapon().isItemTypeAllowed(target)
                        && !(isTargetBot && getServices().getBaseService().isBot(attacker.getBase()))) {
                    attackers.add(attacker);
                }
            }
        }
        for (SyncBaseItem attacker : attackers) {
            defend(attacker, target);
        }
    }

    private boolean checkGuardingItemHasEnemiesInRange(SyncBaseItem guardingItem) {
        boolean isBot = getServices().getBaseService().isBot(guardingItem.getBase());
        SyncBaseItem target = getServices().getItemService().getFirstEnemyItemInRange(guardingItem, isBot);
        if (target != null) {
            defend(guardingItem, target);
            return true;
        } else {
            return false;
        }
    }

}
