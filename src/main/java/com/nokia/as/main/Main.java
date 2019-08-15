/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.main;

import com.nokia.as.main.jetty.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sebferrer
 * The main function
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void startJettyServer() {
        App jettyServer = new App();
        Thread thread = new Thread(jettyServer);
        thread.start();
    }

    public static void main(String[] args) {
        try {

            startJettyServer();

        } catch (Exception e) {
            logger.error("Exception in Prometheus Data Collector tool build\n> " +
                    e + "\n" +
                    "> This error is fatal, exiting.");
        }
    }
}