/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.data.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nokia.as.data.connection.timeseries.AbstractTimeseriesConnector;
import com.nokia.as.util.FileUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sebferrer
 * Collects the metrics via the connector and contains
 * all of the configuration parameters.
 */
public class DataCollector {
    private static final Logger logger = LoggerFactory.getLogger(DataCollector.class);
    /**
     * The timeseries connector
     */
    private AbstractTimeseriesConnector timeseriesConnector;
    /**
     * The backward limit to get the data
     */
    private int historyTime;
    /**
     * The forward limit to get the data
     */
    private int maxDuration;
    /**
     * The JSON output directory. If empty, the JSON exportation is disabled
     */
    private String jsonOutputDir;
    /**
     * The CSV output directory. If empty, the CSV exportation is disabled
     */
    private String csvOutputDir;

    public DataCollector(
            String timeseriesConnectorType,
            String timeseriesConnectorSrvAddress,
            int historyTime,
            int maxDuration,
            String jsonOutputFile,
            String csvOutputFile
    ) {
        try {
            timeseriesConnector = AbstractTimeseriesConnector.build(
                    timeseriesConnectorType,
                    timeseriesConnectorSrvAddress);
            this.historyTime = historyTime;
            this.maxDuration = maxDuration;
            this.jsonOutputDir = jsonOutputFile;
            this.csvOutputDir = csvOutputFile;

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            logger.info("Data collector successfully created: " + gson.toJson(this));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DataCollector build(String configFile) {
        JSONObject jsonConfig = new JSONObject(FileUtil.readFile(configFile)).getJSONObject("datacollector");
        return new DataCollector(jsonConfig.getJSONObject("connectors")
                .getJSONObject("datasource")
                .getString("type"),
                jsonConfig.getJSONObject("connectors")
                        .getJSONObject("datasource")
                        .getString("srvaddress"),
                jsonConfig.getInt("historytime"),
                jsonConfig.getInt("maxduration"),
                jsonConfig.getJSONObject("output").getString("json"),
                jsonConfig.getJSONObject("output").getString("csv"));
    }

    public AbstractTimeseriesConnector getTimeseriesConnector() {
        return timeseriesConnector;
    }

    public int getHistoryTime() {
        return historyTime;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public String getJsonOutputDir() {
        return jsonOutputDir;
    }

    public String getCsvOutputDir() {
        return csvOutputDir;
    }
}