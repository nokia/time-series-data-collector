/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.data.timeseries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nokia.as.util.ArrayUtil;
import com.nokia.as.util.DateUtil;
import com.nokia.as.util.FileUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author sebferrer
 * 2 util methods to export timeseries, in CSV or JSON
 * If the query returns more than one timeseries, every timeseries will be written in the same CSV file
 * while one JSON file will be created for each of them
 */
public class TimeSeriesExporter {

    public static void exportCSVFile(TimeseriesCollec timeseriesCollec, String outputDir) {
        if (outputDir.isEmpty()) {
            return;
        }

        ArrayList<ArrayList<Double>> timeseriesValues = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Integer> lengths = new ArrayList<>();

        FileUtil.createDirectory(outputDir);

        String outputFileName = DateUtil.getCurrentMillis() + ".csv";
        if (!"".equals(timeseriesCollec.getId())) {
            outputFileName = timeseriesCollec.getId() + "_" + outputFileName;
        }

        String outputFileFullPath = outputDir + "/" + outputFileName;

        int index = 0;
        for (Timeseries timeseries : timeseriesCollec.getTimeseriesArray()) {
            String key = timeseries.getId();
            keys.add(key);
            timeseriesValues.add(timeseries.getValues());
            lengths.add(timeseriesValues.get(index).size());
            index++;
        }

        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFileFullPath, true);
            bw = new BufferedWriter(fw);
            bw.write("");
            fw = new FileWriter(outputFileFullPath);
            bw = new BufferedWriter(fw);

            for (int i = 0; i < timeseriesValues.size(); i++) {
                if (i < timeseriesValues.size() - 1) {
                    bw.write(keys.get(i) + ";");
                } else {
                    bw.write(keys.get(i) + "\n");
                }
            }

            for (int i = 0; i < ArrayUtil.getMaxInteger(lengths); i++) {
                for (int j = 0; j < timeseriesCollec.getTimeseriesArray().size(); j++) {
                    String value = j < timeseriesValues.size() && i < timeseriesValues.get(j).size() ?
                            timeseriesValues.get(j).get(i) + "" : "";
                    if (j < timeseriesCollec.getTimeseriesArray().size() - 1) {
                        bw.write(value + ";");
                    } else {
                        bw.write(value + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void exportJsonFiles(TimeseriesCollec timeseriesCollec, String outputDir) {
        if (outputDir.isEmpty()) {
            return;
        }

        FileUtil.createDirectory(outputDir);

        BufferedWriter bw = null;
        FileWriter fw = null;
        for (Timeseries timeseries : timeseriesCollec.getTimeseriesArray()) {
            try {
                String id = timeseries.getId() + "_" + DateUtil.getCurrentMillis();
                if (!timeseriesCollec.getId().isEmpty()) {
                    id = timeseriesCollec.getId() + "_" + id;
                }

                String fileName = outputDir + "/" + id + "_" + timeseries.getDate() + ".json";

                fw = new FileWriter(fileName, true);
                bw = new BufferedWriter(fw);
                bw.write("");
                fw = new FileWriter(fileName);
                bw = new BufferedWriter(fw);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                bw.write(gson.toJson(timeseries));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null)
                        bw.close();
                    if (fw != null)
                        fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}