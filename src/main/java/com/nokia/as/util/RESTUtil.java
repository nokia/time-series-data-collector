/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author sebferrer
 * REST util functions
 */
public class RESTUtil {

    private static final Logger logger = LoggerFactory.getLogger(RESTUtil.class);

    public static String get(String link, String mediaType) {
        // MediaType.TEXT_PLAIN
        // MediaType.APPLICATION_JSON
        try {
            StringBuilder answer = new StringBuilder("");
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Accept", mediaType);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                answer.append(output);
            }

            conn.disconnect();

            return answer.toString();
        } catch (MalformedURLException e) {
            logger.error("REST Error: Malformed URL Exception " + link + " (" + mediaType + ")");

        } catch (IOException e) {
            logger.error("REST Error: IO Exception " + link + " (" + mediaType + ")");
        }
        return "";
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static String post(String apiURL, String fileName) {
        try {
            String url = apiURL;

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");

            String data = FileUtil.readFile(fileName, null);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.close();

            InputStreamReader isr = new InputStreamReader(conn.getInputStream());

            BufferedReader rd = new BufferedReader(isr);
            return readAll(rd);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}