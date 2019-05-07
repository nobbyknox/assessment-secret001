package com.nobbyknox.cibecs.server;


import com.nobbyknox.cibecs.commons.api.Comms;
import com.nobbyknox.cibecs.commons.api.Config;
import com.nobbyknox.cibecs.commons.configuration.EnvironmentConfigProvider;
import com.nobbyknox.cibecs.commons.exceptions.ConfigException;
import com.nobbyknox.cibecs.server.communications.MessageHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Server {
    private Logger logger = LogManager.getLogger();

    public Server() {
        // Suppress the chatter of others
        Configurator.setLevel("org", Level.ERROR);

        // Check that all the mandatory parameters/settings have been configured.
        // We cannot run without these.
        try {
            checkConfig();
        } catch (ConfigException exc) {
            logger.fatal(exc);
            printConfigHelp();
            System.exit(1);
        }

        Runnable tcpRunner = () -> {
            try {
                Comms.startServer(Config.getIntConfigValue("tcp.server.port").get());
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        };

        new Thread(tcpRunner).start();


        try {
            Thread.sleep(3000);
            Comms.registerServerReceiveHandler(MessageHandler::handle);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");
            Comms.stopServer();
        }));
    }

    private void checkConfig() throws ConfigException {
        String[] paramNames = {"tcp.server.port"};
        Config.configureWith(new EnvironmentConfigProvider());
        Config.checkConfiguration(paramNames);

    }

    private void printConfigHelp() {
        logger.info("The following parameters are necessary for the correct operation of the system:");
        logger.info("  * tcp.server.port [integer]: The port of the TCP server");
    }

    public static void main(String... args) {
        new Server();
    }
}