package com.btxtech.game.services.action;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBuilder;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncHarvester;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncWeapon;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderFinalizeCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.services.collision.CollisionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 15.05.2011
 * Time: 13:55:14
 */
public class ActionServiceUtil {

    /**
     * Adds the destination hint to collect and move command. If there is not enough space on the terrain
     * the collect and move command are replaced with the move command
     *
     * @param baseCommands     to be checked
     * @param collisionService collision service
     * @param itemService      item service
     */
    public static void addDestinationHintToCommands(Collection<BaseCommand> baseCommands, CollisionService collisionService, ItemService itemService) {
        Map<Id, List<AttackCommand>> attackCommandMap = new HashMap<Id, List<AttackCommand>>();
        Map<Id, List<MoneyCollectCommand>> moneyCollectCommandMap = new HashMap<Id, List<MoneyCollectCommand>>();
        Map<Id, List<BuilderFinalizeCommand>> builderFinalizeCommandMap = new HashMap<Id, List<BuilderFinalizeCommand>>();

        for (BaseCommand baseCommand : baseCommands) {
            if (baseCommand instanceof AttackCommand) {
                AttackCommand attackCommand = (AttackCommand) baseCommand;
                List<AttackCommand> attackCommands = attackCommandMap.get(attackCommand.getTarget());
                if (attackCommands == null) {
                    attackCommands = new ArrayList<AttackCommand>();
                    attackCommandMap.put(attackCommand.getTarget(), attackCommands);
                }
                attackCommands.add(attackCommand);
            } else if (baseCommand instanceof MoneyCollectCommand) {
                MoneyCollectCommand moneyCollectCommand = (MoneyCollectCommand) baseCommand;
                List<MoneyCollectCommand> moneyCollectCommands = moneyCollectCommandMap.get(moneyCollectCommand.getTarget());
                if (moneyCollectCommands == null) {
                    moneyCollectCommands = new ArrayList<MoneyCollectCommand>();
                    moneyCollectCommandMap.put(moneyCollectCommand.getTarget(), moneyCollectCommands);
                }
                moneyCollectCommands.add(moneyCollectCommand);
            } else if (baseCommand instanceof BuilderFinalizeCommand) {
                BuilderFinalizeCommand builderFinalizeCommand = (BuilderFinalizeCommand) baseCommand;
                List<BuilderFinalizeCommand> builderFinalizeCommands = builderFinalizeCommandMap.get(builderFinalizeCommand.getToBeBuilt());
                if (builderFinalizeCommands == null) {
                    builderFinalizeCommands = new ArrayList<BuilderFinalizeCommand>();
                    builderFinalizeCommandMap.put(builderFinalizeCommand.getToBeBuilt(), builderFinalizeCommands);
                }
                builderFinalizeCommands.add(builderFinalizeCommand);
            }
        }

        Map<Id, BaseCommand> changedCommand = addDestinationHintToAttack(attackCommandMap, collisionService, itemService);
        changedCommand.putAll(addDestinationHintToCollect(moneyCollectCommandMap, collisionService, itemService));
        changedCommand.putAll(addDestinationHintToBuilderFinalize(builderFinalizeCommandMap, collisionService, itemService));
        for (Iterator<BaseCommand> iterator = baseCommands.iterator(); iterator.hasNext();) {
            BaseCommand baseCommand = iterator.next();
            if (changedCommand.containsKey(baseCommand.getId())) {
                iterator.remove();
            }
        }
        baseCommands.addAll(changedCommand.values());
    }

    private static Map<Id, BaseCommand> addDestinationHintToAttack(Map<Id, List<AttackCommand>> attackCommandMap, CollisionService collisionService, ItemService itemService) {
        Map<Id, BaseCommand> changedCommand = new HashMap<Id, BaseCommand>();
        for (Map.Entry<Id, List<AttackCommand>> entry : attackCommandMap.entrySet()) {
            SyncItem target;
            try {
                target = itemService.getItem(entry.getKey());
            } catch (ItemDoesNotExistException e) {
                // The target does not exist anymore
                continue;
            }

            List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
            for (AttackCommand attackCommand : entry.getValue()) {
                try {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.getItem(attackCommand.getId());
                    SyncWeapon syncWeapon = syncBaseItem.getSyncWeapon();
                    attackFormationItemList.add(new AttackFormationItem(syncBaseItem, syncWeapon.getWeaponType().getRange()));
                } catch (ItemDoesNotExistException e) {
                    // May be killed
                }
            }

            attackFormationItemList = collisionService.setupDestinationHints(target, attackFormationItemList);

            for (int i = 0; i < attackFormationItemList.size(); i++) {
                AttackCommand attackCommand = entry.getValue().get(i);
                AttackFormationItem item = attackFormationItemList.get(i);
                if (item.isInRange()) {
                    attackCommand.setDestinationHint(item.getDestinationHint());
                    attackCommand.setDestinationAngel(item.getDestinationAngel());
                } else {
                    changedCommand.put(item.getSyncBaseItem().getId(), createMoveCommand(item.getSyncBaseItem().getId(), item.getDestinationHint()));
                }
            }
        }
        return changedCommand;
    }

    private static Map<Id, BaseCommand> addDestinationHintToCollect(Map<Id, List<MoneyCollectCommand>> moneyCollectCommandMap, CollisionService collisionService, ItemService itemService) {
        Map<Id, BaseCommand> changedCommand = new HashMap<Id, BaseCommand>();
        for (Map.Entry<Id, List<MoneyCollectCommand>> entry : moneyCollectCommandMap.entrySet()) {
            SyncItem target;
            try {
                target = itemService.getItem(entry.getKey());
            } catch (ItemDoesNotExistException e) {
                // The target does not exist anymore
                continue;
            }

            List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
            for (MoneyCollectCommand moneyCollectCommand : entry.getValue()) {
                try {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.getItem(moneyCollectCommand.getId());
                    SyncHarvester syncHarvester = syncBaseItem.getSyncHarvester();
                    attackFormationItemList.add(new AttackFormationItem(syncBaseItem, syncHarvester.getHarvesterType().getRange()));
                } catch (ItemDoesNotExistException e) {
                    // May be killed
                }
            }

            attackFormationItemList = collisionService.setupDestinationHints(target, attackFormationItemList);

            for (int i = 0; i < attackFormationItemList.size(); i++) {
                MoneyCollectCommand moneyCollectCommand = entry.getValue().get(i);
                AttackFormationItem item = attackFormationItemList.get(i);
                if (item.isInRange()) {
                    moneyCollectCommand.setDestinationHint(item.getDestinationHint());
                    moneyCollectCommand.setDestinationAngel(item.getDestinationAngel());
                } else {
                    changedCommand.put(item.getSyncBaseItem().getId(), createMoveCommand(item.getSyncBaseItem().getId(), item.getDestinationHint()));
                }
            }
        }
        return changedCommand;
    }

    private static Map<Id, BaseCommand> addDestinationHintToBuilderFinalize(Map<Id, List<BuilderFinalizeCommand>> builderFinalizeCommandMap, CollisionService collisionService, ItemService itemService) {
        Map<Id, BaseCommand> changedCommand = new HashMap<Id, BaseCommand>();
        for (Map.Entry<Id, List<BuilderFinalizeCommand>> entry : builderFinalizeCommandMap.entrySet()) {
            SyncItem toBeBuilt;
            try {
                toBeBuilt = itemService.getItem(entry.getKey());
            } catch (ItemDoesNotExistException e) {
                // The target does not exist anymore
                continue;
            }

            List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
            for (BuilderFinalizeCommand finalizeCommand : entry.getValue()) {
                try {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.getItem(finalizeCommand.getId());
                    SyncBuilder syncBuilder = syncBaseItem.getSyncBuilder();
                    attackFormationItemList.add(new AttackFormationItem(syncBaseItem, syncBuilder.getBuilderType().getRange()));
                } catch (ItemDoesNotExistException e) {
                    // May be killed
                }
            }

            attackFormationItemList = collisionService.setupDestinationHints(toBeBuilt, attackFormationItemList);

            for (int i = 0; i < attackFormationItemList.size(); i++) {
                BuilderFinalizeCommand finalizeCommand = entry.getValue().get(i);
                AttackFormationItem item = attackFormationItemList.get(i);
                if (item.isInRange()) {
                    finalizeCommand.setDestinationHint(item.getDestinationHint());
                    finalizeCommand.setDestinationAngel(item.getDestinationAngel());
                } else {
                    changedCommand.put(item.getSyncBaseItem().getId(), createMoveCommand(item.getSyncBaseItem().getId(), item.getDestinationHint()));
                }
            }
        }
        return changedCommand;
    }

    private static MoveCommand createMoveCommand(Id id, Index destination) {
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setTimeStamp();
        moveCommand.setId(id);
        moveCommand.setDestination(destination);
        return moveCommand;
    }
}
