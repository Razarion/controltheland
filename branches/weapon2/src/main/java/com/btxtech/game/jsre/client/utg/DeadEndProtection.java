package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 22.04.13
 * Time: 14:34
 */
abstract public class DeadEndProtection {
    private Collection<BaseItemType> builderItemTypes = new ArrayList<BaseItemType>();
    private Collection<BaseItemType> harvesterItemTypes = new ArrayList<BaseItemType>();
    private DeadEndListener deadEndListener;
    private boolean running;
    private boolean hasHarvester;
    private boolean hasMoney4Harvester;
    private boolean itemDeadEnd;
    private boolean moneyDeadEnd;
    private int harvesterPrice;

    protected abstract ItemService getItemService();

    protected abstract ItemTypeService getItemTypeService();

    protected abstract SimpleBase getMyBase();

    protected abstract boolean isBaseDead();

    protected abstract int getMyMoney();

    protected abstract boolean isSuppressed();

    public void setDeadEndListener(DeadEndListener deadEndListener) {
        this.deadEndListener = deadEndListener;
    }

    public void start() {
        if(isSuppressed()){
            return;
        }
        running = true;
        itemDeadEnd = false;
        moneyDeadEnd = false;
        builderItemTypes.clear();
        harvesterItemTypes.clear();
        harvesterPrice = Integer.MAX_VALUE;
        for (ItemType itemType : getItemTypeService().getItemTypes()) {
            if (itemType instanceof BaseItemType) {
                BaseItemType baseItemType = (BaseItemType) itemType;
                if (baseItemType.getBuilderType() != null || baseItemType.getFactoryType() != null) {
                    builderItemTypes.add(baseItemType);
                }
                if (baseItemType.getHarvesterType() != null && baseItemType.getPrice() > 0) {
                    harvesterItemTypes.add(baseItemType);
                    harvesterPrice = Math.min(baseItemType.getPrice(), harvesterPrice);
                }
            }
        }
        if (harvesterPrice == Integer.MAX_VALUE) {
            harvesterPrice = 0;
        }

        checkForItemDeadEnd(getMyBase());
        checkHasHarvester(getMyBase());
        onMoneyChanged(getMyMoney());
    }

    public void stop() {
        running = false;
        if (deadEndListener != null) {
            if (itemDeadEnd) {
                deadEndListener.revokeItemDeadEnd();
            } else if (moneyDeadEnd) {
                deadEndListener.revokeMoneyDeadEnd();
            }
        }
        itemDeadEnd = false;
        moneyDeadEnd = false;
    }

    public void onSyncItemLost(SyncBaseItem syncBaseItem) {
        if (!running) {
            return;
        }
        SimpleBase simpleBase = syncBaseItem.getBase();
        if (!itemDeadEnd) {
            if (syncBaseItem.hasSyncBuilder() || syncBaseItem.hasSyncFactory()) {
                checkForItemDeadEnd(simpleBase);
            }
        }
        if (hasHarvester) {
            if (syncBaseItem.hasSyncHarvester()) {
                checkHasHarvester(simpleBase);
                checkHarvesterAlert();
            }
        }
    }

    public void onSyncItemCreated(SyncBaseItem syncBaseItem) {
        if (!running) {
            return;
        }
        if (itemDeadEnd) {
            if (syncBaseItem.hasSyncBuilder() || syncBaseItem.hasSyncFactory()) {
                itemDeadEnd = false;
                fireListener(true, false);
            }
        }
        if (!hasHarvester) {
            if (syncBaseItem.hasSyncHarvester()) {
                hasHarvester = true;
                checkHarvesterAlert();
            }
        }
    }

    public void onMoneyChanged(int accountBalance) {
        if (!running) {
            return;
        }
        hasMoney4Harvester = harvesterPrice <= accountBalance;
        checkHarvesterAlert();
    }

    private void checkHarvesterAlert() {
        boolean tmpMoneyDeadEnd = !hasMoney4Harvester && !hasHarvester;
        if (tmpMoneyDeadEnd != moneyDeadEnd) {
            if (tmpMoneyDeadEnd) {
                if (!isBaseDead()) {
                    moneyDeadEnd = true;
                    fireListener(false, true);
                }
            } else {
                moneyDeadEnd = false;
                fireListener(false, true);
            }
        }

    }

    private void fireListener(boolean itemChanged, boolean moneyChanged) {
        if (deadEndListener != null) {
            if (itemChanged) {
                if (itemDeadEnd) {
                    if (moneyDeadEnd) {
                        deadEndListener.revokeMoneyDeadEnd();
                    }
                    deadEndListener.activateItemDeadEnd();
                } else {
                    deadEndListener.revokeItemDeadEnd();
                    if (moneyDeadEnd) {
                        deadEndListener.activateMoneyDeadEnd();
                    }
                }
            } else if (moneyChanged) {
                if (!itemDeadEnd) {
                    if (moneyDeadEnd) {
                        deadEndListener.activateMoneyDeadEnd();
                    } else {
                        deadEndListener.revokeMoneyDeadEnd();
                    }
                }
            }
        }
    }

    private void checkHasHarvester(SimpleBase simpleBase) {
        hasHarvester = false;
        for (BaseItemType harvesterTypes : harvesterItemTypes) {
            if (!getItemService().getItems4BaseAndType(simpleBase, harvesterTypes.getId()).isEmpty()) {
                hasHarvester = true;
                return;
            }
        }
    }

    private void checkForItemDeadEnd(SimpleBase simpleBase) {
        for (BaseItemType builderItemType : builderItemTypes) {
            if (!getItemService().getItems4BaseAndType(simpleBase, builderItemType.getId()).isEmpty()) {
                return;
            }
        }
        if (!isBaseDead()) {
            itemDeadEnd = true;
            fireListener(true, false);
        }
    }
}
