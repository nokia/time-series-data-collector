/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.data.timeseries;

import com.google.gson.annotations.Expose;
import com.nokia.as.util.ArrayUtil;
import com.nokia.as.util.DateUtil;

import java.util.ArrayList;

/**
 * @author sebferrer
 * A simple timeseries
 */
public class Timeseries {

    @Expose
    private String id;
    @Expose
    private long start;
    @Expose
    private long end;
    @Expose
    private String duration;
    @Expose
    private String date;
    @Expose
    private double min;
    @Expose
    private double max;
    @Expose
    private double avg;
    @Expose
    private ArrayList<Double> values;

    public Timeseries(String id, long start, long end, ArrayList<Double> values) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.values = new ArrayList<>(values);
    }

    public void init() {
        this.min = ArrayUtil.getMinDouble(this.values);
        this.max = ArrayUtil.getMaxDouble(this.values);
        this.avg = ArrayUtil.getAvgDouble(this.values);
        this.duration = DateUtil.getTimestampDuration((this.end - this.start));
        this.date = DateUtil.formatTimestamp(this.start);
    }

    public String getId() {
        return this.id;
    }

    public long getStart() {
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

    public String getDuration() {
        return this.duration;
    }

    public String getDate() {
        return this.date;
    }

    public double getMax() {
        return max;
    }

    public double getAvg() {
        return avg;
    }

    public ArrayList<Double> getValues() {
        return this.values;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = new ArrayList<Double>(values);
    }

    public void add(ArrayList<Double> values) {
        this.values.addAll(values);
    }

    public void addTimeseries(Timeseries timeseries) {
        this.values.addAll(timeseries.getValues());
    }

    public void addTimeseriesBefore(Timeseries timeseries) {
        timeseries.getValues().addAll(this.getValues());
        this.setValues(timeseries.getValues());
    }

}