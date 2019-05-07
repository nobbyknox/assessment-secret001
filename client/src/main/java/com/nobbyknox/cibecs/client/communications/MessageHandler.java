package com.nobbyknox.cibecs.client.communications;

import com.nobbyknox.cibecs.commons.communications.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageHandler {
    private static Logger logger = LogManager.getLogger();

    public static void handle(Message message) {
        logger.debug("Client received message: " + message);
    }
}
