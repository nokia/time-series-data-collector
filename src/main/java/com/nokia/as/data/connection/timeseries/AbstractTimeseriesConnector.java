/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.data.connection.timeseries;

import com.nokia.as.data.timeseries.TimeseriesCollec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sebferrer
 * Generic timeseries connector. Connect to a data source to extract timeseries.
 */
public abstract class AbstractTimeseriesConnector {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTimeseriesConnector.class);
    /**
     * IP address + port of the data source.
     */
    String srvAddress;

    public AbstractTimeseriesConnector(
            String srvAddress
    ) {
        this.srvAddress = srvAddress;
    }

    /**
     * Gets timeseries only from a prometheus query and no timestamp information.
     * Recovers the data backward up to the history time.
     *
     * @param query              data source query (e.g Prometheus query).
     * @param historyTime        The history time (backward connections limit).
     * @param reduceHttpRequests If true, will stop to get the timeseries when it seems to have no more data
     * @return A collection of timeseries.
     */
    public abstract TimeseriesCollec getHistoryTimeseries(String query,
                                                          int historyTime,
                                                          boolean reduceHttpRequests);

    /**
     * Gets timeseries from a prometheus query considering its start time.
     * Recovers the data backward up to the history time.
     *
     * @param query              data source query (e.g Prometheus query).
     * @param startTime          Gets the data from this timestamp.
     * @param maxDuration        As the endTimestamp isn't set, it will do connections until startTime + maxDuration.
     * @param reduceHttpRequests If true, will stop to get the timeseries when it seems to have no more data
     * @return A collection of timeseries.
     */
    public abstract TimeseriesCollec getTimeseriesFromStart(String query,
                                                            long startTime,
                                                            int maxDuration,
                                                            boolean reduceHttpRequests);

    /**
     * Gets timeseries from a prometheus query considering its time range (start - end).
     *
     * @param query     data source query (e.g Prometheus query).
     * @param startTime Gets the data from this timestamp.
     * @param endTime   Gets the data from startTime to this timestamp.
     * @return A collection of timeseries.
     */
    public abstract TimeseriesCollec getTimeseries(String query,
                                                   long startTime,
                                                   long endTime);

    /**
     * Checks if the server is ready to provide data
     *
     * @param query e.g Prometheus query
     * @return true if the data source is ready, false if not
     */
    public abstract boolean isReady(String query);

    /**
     * Generate an URL from a query
     *
     * @param query data source query (e.g Prometheus query).
     * @return the String URL
     */
    public abstract String getUrl(String query);

    /**
     * Generate an URL from a query, start time, end time and a step
     *
     * @param query data source query (e.g Prometheus query).
     * @param start Start time
     * @param end   End time
     * @param step  Step resolution
     * @return the String URL
     */
    public abstract String getUrl(String query, long start, long end, int step);

    /**
     * Factory builder
     *
     * @param type       Date source type (e.g Prometheus)
     * @param srvAddress
     * @return A specialization of AbstractTimeseriesConnector
     * @throws ClassNotFoundException
     */
    public static AbstractTimeseriesConnector build(
            String type,
            String srvAddress) throws ClassNotFoundException {
        switch (type) {
            case PrometheusConnector.type:
                return new PrometheusConnector(srvAddress);
            default:
                throw new ClassNotFoundException("Connector type not found " + type);
        }
    }
}