package com.btxtech.game.services.finance;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * User: beat
 * Date: 11.02.14
 * Time: 22:09
 */
@XmlRootElement
public class FacebookPaymentUpdate {
    private String object;
    private Entry[] entry;

    public Entry[] getEntry() {
        return entry;
    }

    public void setEntry(Entry[] entry) {
        this.entry = entry;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "FacebookPaymentUpdate{" +
                "entry=" + Arrays.toString(entry) +
                ", object='" + object + '\'' +
                '}';
    }

    public static class Entry {
        private String id;
        private String time;
        private String[] changed_fields;

        public String[] getChanged_fields() {
            return changed_fields;
        }

        public void setChanged_fields(String[] changed_fields) {
            this.changed_fields = changed_fields;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "changed_fields=" + Arrays.toString(changed_fields) +
                    ", id='" + id + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }
}
