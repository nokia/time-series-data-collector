/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.main.jetty;

import com.nokia.as.data.connection.DataCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * @author sebferrer
 * Jetty App runtime
 * Uncomment all that's commented to enable SSL if you created your own keystore.jks
 */
public class App implements Runnable {
    public static DataCollector collector;

    public App() {
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

        collector = DataCollector.build("resources/config.json");
    }

    public void run() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server();
        jettyServer.setHandler(context);

        ServerConnector connector = new ServerConnector(jettyServer);
        connector.setPort(JettyConfig.HTTP_PORT);

        if (JettyConfig.ENABLE_SSL) {
            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());

            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(JettyConfig.KEYSTORE_FILE);
            sslContextFactory.setKeyStorePassword(JettyConfig.KEYSTORE_PWD);
            sslContextFactory.setKeyManagerPassword(JettyConfig.KEYSTORE_MANAGER_PWD);

            ServerConnector sslConnector = new ServerConnector(jettyServer,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(https));
            sslConnector.setPort(JettyConfig.HTTPS_PORT);

            jettyServer.setConnectors(new ServerConnector[]{connector, sslConnector});
        } else {
            jettyServer.setConnectors(new ServerConnector[]{connector});
        }

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                EntryPoint.class.getCanonicalName());

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }
}