/*
 * Created on 07-Sep-2004
 * @author bandara
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import common.*;

import java.rmi.registry.Registry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.logging.*;

/**
 * @author bandara
 *
 */
public class RMIServer extends UnicastRemoteObject implements RMIServerI {
    private final static Logger logger =
        Logger.getLogger(RMIServer.class.getName());
    private static FileHandler fh = null;

	private int totalMessages = -1;
	private int[] receivedMessages;
    private int cnt_msg_recv = 0;

    private static int registry_port = 1099;

	public RMIServer() throws RemoteException {
	}

	public void receiveMessage(MessageInfo msg) throws RemoteException {

		// TO-DO: On receipt of first message, initialise the receive buffer
        if (totalMessages == -1) {
            totalMessages = msg.totalMessages;
            cnt_msg_recv = 0;
            receivedMessages = new int[totalMessages];
        }

		// TO-DO: Log receipt of the message
        logger.info("Message " + String.format("%4d", msg.messageNum)
                + " received");
        receivedMessages[msg.messageNum] = 1;
        cnt_msg_recv++;

		// TO-DO: If this is the last expected message, then identify
		//        any missing messages
        if (cnt_msg_recv == totalMessages) {
            logSummary();

            // unbind
            try {
                Naming.unbind("rmi://localhost/RMIServer");
                unexportObject(this, true);
            } catch (Exception e) {}
        }
	}

    private void logSummary() {
        logger.info("");
        logger.info("================ START OF SUMMARY ================");
        if (totalMessages == -1) {
            logger.warning("No messages have been received");
        }
        else {
            logger.info("n_total_messages    = " +
                    Integer.toString(totalMessages));
            logger.info("n_messages_received = " +
                    Integer.toString(cnt_msg_recv));

            if (cnt_msg_recv == totalMessages) {
                logger.info("All messages successfully received!");
            }
            else {
                String msg_missed = new String();
                for (int i = 0; i < totalMessages; i++) {
                    if (receivedMessages[i] == 0) {
                        if (msg_missed.length() != 0) {
                            msg_missed += ", ";
                        }
                        msg_missed += Integer.toString(i);
                    }
                }
                logger.info("messages_missed     = " + msg_missed);
            }
        }
        logger.info("================= END OF SUMMARY =================");
    }

    private static void initLogFile() {
        try {
            fh = new FileHandler("rmiserver.log");
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        }
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

	public static void main(String[] args) {

		RMIServer rmis = null;

        initLogFile();
        // TO-DO: Initialise Security Manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // TO-DO: Instantiate the server class
        try {
            rmis = new RMIServer();

        } catch (RemoteException e) {
            logger.severe("Exception:" + e.getMessage());
            System.exit(-1);
        }

        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = new String();
        }
        logger.info("RMI server ready, running on " + hostname);

        // TO-DO: Bind to RMI registry
        String url = new String("rmi://localhost/RMIServer");
        rebindServer(url, rmis);
    }

    protected static void rebindServer(String serverURL, RMIServer server) {

        // TO-DO:
        // Start / find the registry (hint use LocateRegistry.createRegistry(...)
        // If we *know* the registry is running we could skip this (eg run rmiregistry in the start script)
        // NOTE: rmiregistry is run the the start script

        // TO-DO:
        // Now rebind the server to the registry (rebind replaces any existing servers bound to the serverURL)
        // Note - Registry.rebind (as returned by createRegistry / getRegistry) does something similar but
        // expects different things from the URL field.
        try {
            Naming.rebind(serverURL, server);
        } catch (RemoteException|MalformedURLException e) {
            logger.severe("Exception:" + e.getMessage());
        }
    }
}
