package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */
@Component(value = "inventoryService")
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private CrudRootServiceHelper<DbInventoryArtifact> artifactCrud;
    @Autowired
    private CrudRootServiceHelper<DbInventoryItem> itemCrud;

    @PostConstruct
    public void ini() {
        artifactCrud.init(DbInventoryArtifact.class);
        itemCrud.init(DbInventoryItem.class);
    }

    @Override
    public CrudRootServiceHelper<DbInventoryArtifact> getArtifactCrud() {
        return artifactCrud;
    }

    @Override
    public CrudRootServiceHelper<DbInventoryItem> getItemCrud() {
        return itemCrud;
    }
}
