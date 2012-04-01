package com.btxtech.game.services.statistics.impl;

import com.btxtech.game.services.common.ContentSort;
import com.btxtech.game.services.common.ContentSortList;
import com.btxtech.game.services.common.NestedNullSafeBeanComparator;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.statistics.CurrentStatisticEntry;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: beat
 * Date: 27.03.2012
 * Time: 21:06:35
 */
public class CurrentStatisticServiceContentProvider extends ReadonlyListContentProvider<CurrentStatisticEntry> {
    private List<CurrentStatisticEntry> entries;

    public CurrentStatisticServiceContentProvider(List<CurrentStatisticEntry> entries) {
        super(Collections.<CurrentStatisticEntry>emptyList());
        this.entries = entries;
    }

    @Override
    public List<CurrentStatisticEntry> readDbChildren() {
        return entries;
    }

    @Override
    public Collection<CurrentStatisticEntry> readDbChildren(ContentSortList contentSortList) {
        if (contentSortList == null) {
            return entries;
        }
        final ContentSort contentSort = contentSortList.getContentSorts().get(0);
        final NestedNullSafeBeanComparator beanComparator = new NestedNullSafeBeanComparator(contentSort.getPropertyName(), false);
        Collections.sort(entries, new Comparator<CurrentStatisticEntry>() {
            @Override
            public int compare(CurrentStatisticEntry o1, CurrentStatisticEntry o2) {
                if (contentSort.isAsc()) {
                    return beanComparator.compare(o1, o2);
                } else {
                    return beanComparator.compare(o2, o1);
                }
            }
        });
        int rank = 1;
        for (CurrentStatisticEntry entry : entries) {
            if (contentSort.isAsc()) {
                entry.setRank(entries.size() - rank + 1);
            } else {
                entry.setRank(rank);
            }
            rank++;
        }
        return entries;
    }
}
