package com.armchairfun;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * The PolicyServer waits for client connections and uses PolicyServerConnections to handle policy requests.
 *
 */
public class PolicyServer extends Thread {
    public static final String POLICY_REQUEST = "<policy-file-request/>";
    public static final String POLICY_XML =
            "<?xml version=\"1.0\"?>"
            + "<cross-domain-policy>"
            + "<allow-access-from domain=\"*\" to-ports=\"*\" />"
            + "</cross-domain-policy>";
    
    protected int port;
    protected ServerSocket serverSocket;
    protected boolean listening;
    protected boolean allDone = false;
    
    /**
     * Creates a new instance of PolicyServer.
     *
     * @param serverPort the port to be used by the server
     */
    public PolicyServer(int serverPort) {
        this.port = serverPort;
        this.listening = false;
    }
    
    /**
     * Gets the server's port.
     *
     * @return the port of the server
     */
    public int getPort() {
        return this.port;
    }
    
    /**
     * Gets the server's listening status.
     *
     * @return true if the server is listening
     */
    public boolean getListening() {
        return this.listening;
    }
    
    /**
     * Roots a debug message to the main application.
     * 
     * @param msg the debug message to be sent to the main application
     */
    protected void debug(String msg) {
        System.out.println("PolicyServer (" + this.port + ")"+ ": "+  msg);
    }
    
    /**
     * Waits for clients' connections and handles them to a new PolicyServerConnection.
     */
    public void run() {
        try {
        	if (allDone) {
        		return;
        	}
        	
            this.serverSocket = new ServerSocket(this.port);
            this.listening = true;
            debug("listening");
            
            while (this.listening && !allDone) {
                Socket socket = this.serverSocket.accept();
                debug("client connection from " + socket.getRemoteSocketAddress());
                PolicyServerConnection socketConnection = new PolicyServerConnection(socket);
                socketConnection.start();
            };
        }
        catch (Exception e) {
            debug("Exception (run): " + e.getMessage());
        }
    }
    
    /**
     * Closes the server's socket.
     */
    protected void finalize() {	 
        try {
            this.serverSocket.close();
            this.listening = false;
            debug("stopped");
        }
        catch (Exception e) {
            debug("Exception (finalize): " + e.getMessage());
        }
    }
}
