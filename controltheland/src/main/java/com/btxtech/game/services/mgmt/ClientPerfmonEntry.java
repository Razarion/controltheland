package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * User: beat
 * Date: 27.07.12
 * Time: 00:28
 */
public class ClientPerfmonEntry {
    public static final String REST = "rest";
    private PerfmonEnum perfmonEnum;
    private Integer time;
    private List<ChildClientPerfmonEntry> children;
    private static Log log = LogFactory.getLog(ClientPerfmonEntry.class);

    public ClientPerfmonEntry(PerfmonEnum perfmonEnum, Integer time, Map<String, Integer> childWorkMap) {
        this.perfmonEnum = perfmonEnum;
        this.time = time;
        if (childWorkMap != null) {
            children = new ArrayList<>();
            int totalChildWorkTime = 0;
            for (Map.Entry<String, Integer> entry : childWorkMap.entrySet()) {
                totalChildWorkTime += entry.getValue();
                children.add(new ChildClientPerfmonEntry(entry.getKey(), entry.getValue()));
            }
            if (totalChildWorkTime > time) {
                log.warn("ClientPerfmonEntry: total tim of children is bigger that the total time. Children: " + totalChildWorkTime + " time: " + time);
                this.time = totalChildWorkTime;
            } else if (totalChildWorkTime < time) {
                children.add(new ChildClientPerfmonEntry(REST, time - totalChildWorkTime));
            }
            Collections.sort(children, new Comparator<ClientPerfmonEntry.ChildClientPerfmonEntry>() {
                @Override
                public int compare(ClientPerfmonEntry.ChildClientPerfmonEntry o1, ClientPerfmonEntry.ChildClientPerfmonEntry o2) {
                    return Integer.compare(o2.getTime(), o1.getTime());
                }
            });
        }
    }

    public PerfmonEnum getPerfmonEnum() {
        return perfmonEnum;
    }

    public Integer getTime() {
        return time;
    }

    public List<ChildClientPerfmonEntry> getSortedChildrenAndRest() {
        return children;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public static class ChildClientPerfmonEntry {
        private String childName;
        private Integer time;

        public ChildClientPerfmonEntry(String childName, Integer time) {
            this.childName = childName;
            this.time = time;
        }

        public String getChildName() {
            return childName;
        }

        public Integer getTime() {
            return time;
        }
    }
}
