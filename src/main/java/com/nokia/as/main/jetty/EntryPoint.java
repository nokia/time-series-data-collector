/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.main.jetty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nokia.as.data.timeseries.TimeSeriesExporter;
import com.nokia.as.data.timeseries.TimeseriesCollec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

/**
 * @author sebferrer
 * Entry points for the Jetty server
 */
@Path("/collector/service")
public class EntryPoint {

    /**
     * @param query              Is the data source query (e.g Prometheus query).
     * @param id                 Is an optional parameter to name the generated files.
     * @param start              The connector will get the data from this start.
     *                           If it's not set, the connector will get the data backward
     *                           from the current timestamp until the historyTime set into the config.json
     * @param end                The connector will get the data from start to this end.
     *                           If it's not set, the connector will get the data from until
     *                           the maxDuration set into the config.json.
     * @param reduceHttpRequests If true, stop the connections when the timeseries seems to be finished.
     *                           If false, do all the connections anyway
     * @return a timeseries or a collection of timeseries considering a prometheus query.
     */
    @GET
    @Path("get_ts")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTimeseries(@QueryParam("query") String query,
                                @QueryParam("id") String id,
                                @QueryParam("start") Long start,
                                @QueryParam("end") Long end,
                                @QueryParam("reducehttprequests") Boolean reduceHttpRequests) {

        id = Objects.toString(id, "");
        reduceHttpRequests = reduceHttpRequests == null || reduceHttpRequests;

        TimeseriesCollec timeseriesCollec;

        if (start != null && end != null) {
            timeseriesCollec = App.collector.getTimeseriesConnector().getTimeseries(
                    query,
                    start,
                    end);
        } else if (start != null) {
            timeseriesCollec = App.collector.getTimeseriesConnector().getTimeseriesFromStart(
                    query,
                    start,
                    App.collector.getMaxDuration(),
                    reduceHttpRequests);
        } else {
            timeseriesCollec = App.collector.getTimeseriesConnector().getHistoryTimeseries(
                    query,
                    App.collector.getHistoryTime(),
                    reduceHttpRequests);
        }

        if (timeseriesCollec == null) {
            return "{}";
        }

        timeseriesCollec.setId(id);

        TimeSeriesExporter.exportJsonFiles(timeseriesCollec, App.collector.getJsonOutputDir());
        TimeSeriesExporter.exportCSVFile(timeseriesCollec, App.collector.getCsvOutputDir());

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.excludeFieldsWithoutExposeAnnotation().create();

        return gson.toJson(timeseriesCollec);
    }
}