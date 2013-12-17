package com.btxtech.game.jsre.common.gameengine.services.action.impl;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
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
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.PickupBoxCommand;
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

    protected abstract GlobalServices getGlobalServices();

    protected abstract PlanetServices getPlanetServices();

    protected boolean checkCommand(SyncItem syncItem) {
        Id id = syncItem.getId();
        if (id == null) {
            log.severe("Can not send command: id is null");
            return true;
        }
        return false;
    }

    protected boolean moveIfPathTargetUnreachable(SyncBaseItem syncBaseItem, Path path) {
        if (path.isDestinationReachable()) {
            return false;
        } else {
            move(syncBaseItem, path.getAlternativeDestination());
            return true;
        }
    }

    @Override
    public void move(SyncBaseItem syncBaseItem, Index destination) {
        syncBaseItem.stop();
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(syncBaseItem.getId());
        moveCommand.setTimeStamp();
        moveCommand.setPathToDestination(getPlanetServices().getCollisionService().setupPathToDestination(syncBaseItem, destination));
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
        Path path;
        AttackCommand attackCommand = new AttackCommand();
        if (followTarget) {
            path = getPlanetServices().getCollisionService().setupPathToDestination(syncBaseItem, destinationHint);
            if (moveIfPathTargetUnreachable(syncBaseItem, path)) {
                return;
            }
            path.setDestinationAngel(destinationAngel);
            attackCommand.setPathToDestination(path);
        }
        attackCommand.setId(syncBaseItem.getId());
        attackCommand.setTimeStamp();
        attackCommand.setTarget(target.getId());
        attackCommand.setFollowTarget(followTarget);

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
        AttackFormationItem format = getPlanetServices().getCollisionService().getDestinationHint(syncItem,
                syncItem.getSyncBuilder().getBuilderType().getRange(),
                toBeBuilt.getBoundingBox().createSyntheticSyncItemArea(positionToBeBuild));
        if (format.isInRange()) {
            Path path = getPlanetServices().getCollisionService().setupPathToDestination(syncItem, format.getDestinationHint());
            if (moveIfPathTargetUnreachable(syncItem, path)) {
                return;
            }
            path.setDestinationAngel(format.getDestinationAngel());
            builderCommand.setPathToDestination(path);
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
        Path path = getPlanetServices().getCollisionService().setupPathToDestination(syncItem, destinationHint);
        if (moveIfPathTargetUnreachable(syncItem, path)) {
            return;
        }
        path.setDestinationAngel(destinationAngel);
        builderCommand.setPathToDestination(path);
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
        Path path = getPlanetServices().getCollisionService().setupPathToDestination(builder, destinationHint);
        if (moveIfPathTargetUnreachable(builder, path)) {
            return;
        }
        path.setDestinationAngel(destinationAngel);
        builderFinalizeCommand.setPathToDestination(path);
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
        Path path = getPlanetServices().getCollisionService().setupPathToDestination(collector, destinationHint);
        if (moveIfPathTargetUnreachable(collector, path)) {
            return;
        }
        collectCommand.setPathToDestination(path);
        path.setDestinationAngel(destinationAngel);
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
    public void loadContainer(SyncBaseItem container, SyncBaseItem item, Index destinationHint, double destinationAngel) {
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
        Path path = getPlanetServices().getCollisionService().setupPathToDestination(item, destinationHint);
        if (moveIfPathTargetUnreachable(item, path)) {
            return;
        }
        loadContainCommand.setPathToDestination(path);
        path.setDestinationAngel(destinationAngel);

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

    @Override
    public void pickupBox(SyncBaseItem picker, SyncBoxItem box, Index destinationHint, double destinationAngel) {
        if (checkCommand(picker)) {
            return;
        }
        if (checkCommand(box)) {
            return;
        }

        PickupBoxCommand pickupBoxCommand = new PickupBoxCommand();
        pickupBoxCommand.setId(picker.getId());
        pickupBoxCommand.setBox(box.getId());
        pickupBoxCommand.setTimeStamp();
        Path path = getPlanetServices().getCollisionService().setupPathToDestination(picker, destinationHint);
        if (moveIfPathTargetUnreachable(picker, path)) {
            return;
        }
        pickupBoxCommand.setPathToDestination(path);
        path.setDestinationAngel(destinationAngel);
        try {
            executeCommand(picker, pickupBoxCommand);
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
        try {
            if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
                return;
            }

            if (!(syncTickItem instanceof SyncBaseItem)) {
                return;
            }

            SyncBaseItem syncBaseItem = (SyncBaseItem) syncTickItem;
            if (!syncBaseItem.hasSyncWeapon() || !syncBaseItem.isAlive()) {
                return;
            }

            if (!syncBaseItem.isReady()) {
                return;
            }

            if (syncBaseItem.hasSyncConsumer() && !syncBaseItem.getSyncConsumer().isOperating()) {
                return;
            }

            if (!syncBaseItem.getSyncItemArea().hasPosition()) {
                return;
            }

            if (checkGuardingItemHasEnemiesInRange(syncBaseItem)) {
                return;
            }

            synchronized (guardingItems) {
                guardingItems.add(syncBaseItem);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CommonActionService.addGuardingBaseItem() " + syncTickItem, e);
        }
    }

    @Override
    public void removeGuardingBaseItem(SyncBaseItem syncItem) {
        if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
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
        try {
            if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
                return;
            }
            if (target.isContainedIn()) {
                return;
            }
            if (!target.isAlive()) {
                return;
            }
            // Prevent ConcurrentModificationException
            List<SyncBaseItem> attackers = new ArrayList<SyncBaseItem>();
            synchronized (guardingItems) {
                for (SyncBaseItem attacker : guardingItems) {
                    if (attacker == target) {
                        continue;
                    }
                    if (!attacker.isAlive()) {
                        continue;
                    }
                    if (attacker.isEnemy(target)
                            && attacker.getSyncWeapon().isAttackAllowedWithoutMoving(target)
                            && attacker.getSyncWeapon().isItemTypeAllowed(target)) {
                        attackers.add(attacker);
                    }
                }
            }
            for (SyncBaseItem attacker : attackers) {
                defend(attacker, target);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CommonActionService.interactionGuardingItems(): " + target, e);
        }
    }

    protected boolean checkGuardingItemHasEnemiesInRange(SyncBaseItem guardingItem) {
        SyncBaseItem target = getPlanetServices().getItemService().getFirstEnemyItemInRange(guardingItem);
        if (target != null) {
            defend(guardingItem, target);
            return true;
        } else {
            return false;
        }
    }


}
