/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.BoundedFifoBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: beat
 * Date: 11.10.2010
 * Time: 19:20:02
 */
public abstract class QueueWorker<T> {
    private static final int MAX_QUEUE_SIZE = 10000;
    private static final int INTERVAL = 2000;
    private Buffer buffer;
    private Timer timer;
    private Log log = LogFactory.getLog(QueueWorker.class);

    public QueueWorker() {
        buffer = BufferUtils.synchronizedBuffer(new BoundedFifoBuffer(MAX_QUEUE_SIZE));
        timer = new Timer("QueueWorker timer", true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    work();
                } catch (Throwable throwable) {
                    log.error("", throwable);
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, INTERVAL, INTERVAL);
    }


    @SuppressWarnings("unchecked")
    private void work() {
        ArrayList<T> list = new ArrayList<T>();
        while (!buffer.isEmpty()) {
            list.add((T) buffer.remove());
        }
        if (!list.isEmpty()) {
            processEntries(list);
        }
    }

    @SuppressWarnings("unchecked")
    public void put(T t) {
        if (timer == null) {
            throw new IllegalStateException("Queue is not running");
        }
        buffer.add(t);
    }

    public void stop() {
        if (timer == null) {
            throw new IllegalStateException("Queue is not running");
        }
        timer.cancel();
        timer = null;
        work();
    }

    abstract protected void processEntries(List<T> list);
}
