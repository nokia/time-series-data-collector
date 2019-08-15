/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.data.timeseries;

import com.google.gson.annotations.Expose;
import com.nokia.as.data.connection.timeseries.Direction;

import java.util.ArrayList;

/**
 * @author sebferrer
 * A collection of timeseries
 */
public class TimeseriesCollec {

    @Expose
    private ArrayList<Timeseries> timeseriesArray;
    private String id;

    public TimeseriesCollec() {
        this.timeseriesArray = new ArrayList<Timeseries>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void init() {
        for (Timeseries timeseries : timeseriesArray) {
            timeseries.init();
        }
    }

    public ArrayList<Timeseries> getTimeseriesArray() {
        return timeseriesArray;
    }

    public Timeseries getTimeseries(String id) {
        for (Timeseries timeseries : timeseriesArray) {
            if (id.equals(timeseries.getId())) {
                return timeseries;
            }
        }
        return null;
    }

    public void add(Timeseries timeSeries, Direction direction) {
        if (Direction.FORWARD.equals(direction)) {
            add(timeSeries);
        } else {
            addBefore(timeSeries);
        }
    }

    public void add(Timeseries timeseries) {
        timeseriesArray.add(timeseries);
    }

    public void addBefore(Timeseries newTimeseries) {
        Timeseries timeseries = getTimeseries(newTimeseries.getId());
        if (timeseries == null) {
            this.timeseriesArray.add(newTimeseries);
        } else {
            timeseries.addTimeseriesBefore(newTimeseries);
            timeseries.setStart(newTimeseries.getStart());
        }
    }

}