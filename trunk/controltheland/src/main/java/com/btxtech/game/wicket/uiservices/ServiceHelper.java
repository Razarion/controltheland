package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.wicket.pages.mgmt.items.ItemsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 14.03.2011
 * Time: 20:33:57
 */
@Component
public class ServiceHelper {
    @Autowired
    private ItemService itemService;

    public String itemTypesToString(Set<DbBaseItemType> dbBaseItemTypes) {
        return ItemsUtil.itemTypesToString(dbBaseItemTypes);
    }

    public Set<DbBaseItemType> stringToItemTypes(String itemIdString) {
        if (itemIdString == null) {
            return null;
        }
        Set<DbBaseItemType> result = new HashSet<DbBaseItemType>();
        StringTokenizer st = new StringTokenizer(itemIdString, ItemsUtil.DELIMITER);
        while (st.hasMoreTokens()) {
            int id = Integer.parseInt(st.nextToken());
            result.add((DbBaseItemType) itemService.getDbItemType(id));
        }
        return result;
    }
}
