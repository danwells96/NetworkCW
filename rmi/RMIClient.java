/*
 * Created on 07-Sep-2004
 * @author bandara
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager; // deprecated? use SecurityManger instead?

import common.MessageInfo;

/**
 * @author bandara
 *
 */
public class RMIClient {

	public static void main(String[] args) {

		RMIServerI iRMIServer = null;

		// Check arguments for Server host and number of messages
		if (args.length < 2){
			System.out.println("Needs 2 arguments: ServerHostName/IPAddress, TotalMessageCount");
			System.exit(-1);
		}

		String urlServer = new String("rmi://" + args[0] + "/RMIServer");
		int numMessages = Integer.parseInt(args[1]);

		// TO-DO: Initialise Security Manager
	
		if (System.getSecurityManager()==null)
			System.setSecurityManager(new RMISecurityManager());
		// TO-DO: Bind to RMIServer
		try {
			String name = "sy15Server";
			Registry registry = LocateRegistry.getRegistry(args[0],1099);
			iRMIServer = (RMIServerI) registry.lookup(name);

		// TO-DO: Attempt to send messages the specified number of times
			for (int i=1; i<=numMessages; i++) {
				MessageInfo msg = new MessageInfo(numMessages, i);
				iRMIServer.receiveMessage(msg);
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		



	}
}
