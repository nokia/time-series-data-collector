/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.main.jetty;

/**
 * @author sebferrer
 * All configuration parameters relative to the Jetty runtime
 */
public class JettyConfig {
    public static final int HTTP_PORT = 9995;
    public static final int HTTPS_PORT = 9996;
    public static final boolean ENABLE_SSL = false; // To set to true to enable SSL
    public static final String KEYSTORE_FILE = ""; // To set if ENABLE_SSL is true
    public static final String KEYSTORE_PWD = ""; // To set if ENABLE_SSL is true
    public static final String KEYSTORE_MANAGER_PWD = ""; // To set if ENABLE_SSL is true
}