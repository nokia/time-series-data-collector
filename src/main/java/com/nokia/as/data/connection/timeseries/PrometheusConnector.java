/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.data.connection.timeseries;

import com.nokia.as.data.timeseries.Timeseries;
import com.nokia.as.data.timeseries.TimeseriesCollec;
import com.nokia.as.exception.PrometheusBadStatusException;
import com.nokia.as.util.DateUtil;
import com.nokia.as.util.Pair;
import com.nokia.as.util.RESTUtil;
import com.nokia.as.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author sebferrer
 * Prometheus API connector
 * Uses the Prometheus API to get the data
 */
public class PrometheusConnector extends AbstractTimeseriesConnector {
    public static final String type = "prometheus";
    /**
     * The default limit set by default in most Prometheus instances. It must never be modified.
     */
    private static final int PROMETHEUS_LIMIT = 11000;
    /**
     * The default prometheus API prefix.
     */
    private static final String PROMETHEUS_API = "api/v1/";
    /**
     * The kind of requests available.
     */
    private static final Map<String, String> queryType = new HashMap<String, String>() {{
        put("QUERY", "query");
        put("QUERY_RANGE", "query_range");
    }};

    public PrometheusConnector(String srvAddress) {
        super(srvAddress);
    }

    @Override
    public String getUrl(String query) {
        return "http://" + this.srvAddress + "/" + PROMETHEUS_API + query;
    }

    @Override
    public String getUrl(String query, long start, long end, int step) {
        return getUrl(query) + "&start=" + start + "&end=" + end + "&step=" + step;
    }

    private String formatQuery(String promQuery, String queryType) {
        return queryType + "?query=" + StringUtil.URIencode(promQuery);
    }

    private boolean isBrowsingComplete(long start, long end, ArrayList<Pair<Long, Long>> timeRanges, int currentIndex, Direction direction) {
        return Direction.BACKWARD.equals(direction) && start > timeRanges.get(currentIndex).getLeft() ||
                Direction.FORWARD.equals(direction) && end < timeRanges.get(currentIndex).getRight();
    }

    /**
     * Gets a timeseries or a collection of timeseries considering a prometheus query and an array of time ranges.
     * In order to get more points than the limit allowed by Prometheus (PROMETHEUS_LIMIT), the connections
     * will be splitted by these time ranges.
     *
     * @param promQuery  Prometheus query
     * @param timeRanges Time ranges
     * @param direction  Direction by which it recovers the data (backward or forward)
     *                   If we want to find a short timeserie somewhere in the past without any timestamp information,
     *                   the backward direction can be useful.
     * @return
     */
    private TimeseriesCollec getTimeseriesByTimeRanges(String promQuery,
                                                       ArrayList<Pair<Long, Long>> timeRanges,
                                                       Direction direction,
                                                       boolean reduceHttpRequests) {

        TimeseriesCollec timeseriesCollec = new TimeseriesCollec();
        String url = "";
        try {
            if (!isReady(promQuery)) {
                logger.error("Prometheus error: can't get data for the query " + promQuery);
                throw new PrometheusBadStatusException();
            }

            String query = "";
            int nbTmeseries = 0;

            JSONObject jsonObj;
            JSONObject data = null;
            int firstTimeRangeIndex = 0;
            for (int i = 0; i < timeRanges.size(); i++) {
                if (nbTmeseries > 0) {
                    firstTimeRangeIndex = i - 1;
                    break;
                }
                query = formatQuery(promQuery, queryType.get("QUERY_RANGE"));
                url = this.getUrl(query,
                        timeRanges.get(i).getLeft(),
                        timeRanges.get(i).getRight(),
                        1);
                logger.info("Connect to " + url);
                jsonObj = new JSONObject(RESTUtil.get(url, MediaType.APPLICATION_JSON));
                data = jsonObj.getJSONObject("data");
                nbTmeseries = data.getJSONArray("result").length();
            }

            if (nbTmeseries == 0) {
                logger.error("Prometheus Connector Error: Can't find timeseries for " + url);
                return null;
            }

            logger.info("Timeseries found after " + (firstTimeRangeIndex + 1) + " connections");
            logger.info("Prometheus connection established. Nb timeseries found: " + nbTmeseries);

            for (int i = firstTimeRangeIndex; i < timeRanges.size(); i++) {
                long startTimestamp = 0;
                long endTimestamp = 0;

                if (i > firstTimeRangeIndex) {
                    url = this.getUrl(query,
                            timeRanges.get(i).getLeft(),
                            timeRanges.get(i).getRight(),
                            1);

                    logger.info("Connect to " + url);
                    jsonObj = new JSONObject(RESTUtil.get(url, MediaType.APPLICATION_JSON));

                    if (jsonObj.getJSONObject("data").getJSONArray("result").length() == 0) {
                        if (reduceHttpRequests) {
                            break;
                        }
                        continue;
                    }

                    data = jsonObj.getJSONObject("data");
                    nbTmeseries = data.getJSONArray("result").length();

                    if (nbTmeseries == 0) {
                        if (reduceHttpRequests) {
                            break;
                        }
                        continue;
                    }
                }

                for (int iTimeseries = 0; iTimeseries < nbTmeseries; iTimeseries++) {
                    JSONObject result = data.getJSONArray("result")
                            .getJSONObject(iTimeseries);

                    JSONObject metric = result.getJSONObject("metric");

                    String metricId;
                    if (metric.length() > 0) {
                        Iterator<String> keys = result.getJSONObject("metric").keys();
                        String metricKey = keys.next();
                        metricId = result
                                .getJSONObject("metric")
                                .getString(metricKey);
                    } else {
                        metricId = "" + DateUtil.getCurrentMillis();
                    }

                    JSONArray values = result
                            .getJSONArray("values");

                    ArrayList<Double> metricsTmp = new ArrayList<Double>();
                    for (Object value : values) {
                        metricsTmp.add(Double.parseDouble(((JSONArray) value).getString(1)));
                    }

                    startTimestamp = ((JSONArray) values.get(0)).getInt(0);
                    endTimestamp = ((JSONArray) values.get(values.length() - 1)).getInt(0);

                    if (metricId.isEmpty()) {
                        metricId = "undefined";
                    }

                    Timeseries timeseriesTmp = new Timeseries(
                            metricId,
                            startTimestamp,
                            endTimestamp,
                            metricsTmp);

                    timeseriesCollec.add(timeseriesTmp, direction);
                }

                if (reduceHttpRequests && isBrowsingComplete(startTimestamp, endTimestamp, timeRanges, i, direction)) {
                    break;
                }
            }
            timeseriesCollec.init();
        } catch (
                Exception e) {
            logger.error("Prometheus Connector Error: Can't get timeseries");
        }

        return timeseriesCollec;
    }

    @Override
    public TimeseriesCollec getHistoryTimeseries(String promQuery, int historyTime, boolean reduceHttpRequests) {
        ArrayList<Pair<Long, Long>> timeRanges = DateUtil.getHistoryTimeRanges(
                historyTime,
                PROMETHEUS_LIMIT);

        return getTimeseriesByTimeRanges(promQuery, timeRanges, Direction.BACKWARD, reduceHttpRequests);
    }

    @Override
    public TimeseriesCollec getTimeseriesFromStart(String promQuery,
                                                   long startTime,
                                                   int maxDuration,
                                                   boolean reduceHttpRequests) {
        ArrayList<Pair<Long, Long>> timeRanges = DateUtil.getTimeRangesFromStart(
                maxDuration,
                PROMETHEUS_LIMIT, startTime);

        return getTimeseriesByTimeRanges(promQuery, timeRanges, Direction.FORWARD, reduceHttpRequests);
    }

    @Override
    public TimeseriesCollec getTimeseries(String promQuery, long startTime, long endTime) {
        ArrayList<Pair<Long, Long>> timeRanges = DateUtil.getTimeRanges(startTime, endTime, PROMETHEUS_LIMIT);

        return getTimeseriesByTimeRanges(promQuery, timeRanges, Direction.FORWARD, false);
    }

    @Override
    public boolean isReady(String promQuery) {
        Pair<Long, Long> timeRange = DateUtil.getTimeRange(1);
        String query = formatQuery(promQuery, queryType.get("QUERY"));
        String url = this.getUrl(query, timeRange.getLeft(), timeRange.getRight(), 1);

        try {
            String status = new JSONObject(RESTUtil.get(url, MediaType.APPLICATION_JSON)).getString("status");
            return "success".equals(status);
        } catch (Exception e) {
            logger.error("Prometheus Connector Error: Source " + url + " isn't ready");
        }

        return false;
    }
}