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
import java.rmi.RMISecurityManager;
import java.rmi.registry.*;
import java.net.InetAddress;
import common.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import common.*;

/**
 * @author bandara
 *
 */
public class RMIServer extends UnicastRemoteObject implements RMIServerI {

	private int totalMessages = -1;
	private int[] receivedMessages;

	public RMIServer() throws RemoteException {
		super();
	}

	public void receiveMessage(MessageInfo msg) throws RemoteException {
		// TO-DO: On receipt of first message, initialise the receive buffer
		totalMessages = msg.totalMessages;

		if (totalMessages == -1) {
			receivedMessages = new int[totalMessages];
			for (int i=0; i<totalMessages; i++) {
				receivedMessages[i] = 0;
			}
		}
		
		// TO-DO: Log receipt of the message
		receivedMessages[msg.messageNum-1] = 1;
		System.out.println("Message " + msg.messageNum + " was received");
		

		// TO-DO: If this is the last expected message, then identify
		//        any missing messages
		if(msg.messageNum == totalMessages) {
			System.out.print("The following messages are missed: ");
	
			for(int i = 0; i < totalMessages; i++) {
				if(receivedMessages[i] == 0) {
					System.out.print(i + ", ");
				}
			}
			totalMessages = -1;
		}
	}


	public static void main(String[] args) {

		try {
			//Get IP address and print it to the screen
			InetAddress addr = InetAddress.getLocalHost();
			String ipAddress = addr.getHostAddress();
			System.out.println("Local Host = " + addr);
			System.out.println("Host = " + ipAddress);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try {
			//Initialise RMIServer and set the RMISecurityManager
			RMIServer rmis = null;
			System.setSecurityManager(new RMISecurityManager());
			rmis = new RMIServer();
			//Rebind the RMI Server
			rebindServer("RMIServer", rmis);
		}
		catch (RemoteException e)
		{
			System.out.println("Remote Exception");
		}
	}

	protected static void rebindServer(String serverURL, RMIServer server) {

		// TO-DO:
		try {
			Registry registry;
			try {
				// locate registry and create port at 1099
				registry = LocateRegistry.createRegistry(1099);
			}
			catch (RemoteException e) {
				registry = LocateRegistry.getRegistry();
			}
			// bind
			registry.rebind(serverURL, server);
		}
		catch (Exception e) {
			System.out.println("Error in " + e.getMessage());
		}
		
		// Start / find the registry (hint use LocateRegistry.createRegistry(...)
		// If we *know* the registry is running we could skip this (eg run rmiregistry in the start script)

		// TO-DO:
		// Now rebind the server to the registry (rebind replaces any existing servers bound to the serverURL)
		// Note - Registry.rebind (as returned by createRegistry / getRegistry) does something similar but
		// expects different things from the URL field.
	}
}
