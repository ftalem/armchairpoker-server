package com.armchairfun.poker;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Vector;

/**
 * A Lobby Server for communication between the connected clients.
 */
public class LobbyServer extends Thread {
	protected ServerSocket socketServer;
	protected int port;
	protected boolean listening;
	protected Vector<LobbyServerConnection> clientConnections;
	protected boolean allDone = false;

	/**
	 * Creates a new instance of LobbyServer.
	 * 
	 * @param serverPort
	 *            the port to be used by the server
	 */
	public LobbyServer(int serverPort) {
		this.port = serverPort;
		this.clientConnections = new Vector<LobbyServerConnection>();
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
	 * Gets the number of clients.
	 * 
	 * @return the number of clients
	 */
	public int getClientCount() {
		return this.clientConnections.size();
	}

	/**
	 * Roots a debug message to the main application.
	 * 
	 * @param msg
	 *            the debug message to be sent to the main application
	 */
	protected void debug(String msg) {
		System.out.println("LobbyServer (" + this.port + ")" + ": " + msg);
	}

	/**
	 * Listens for client connections and handles them to
	 * LobbyServerConnections.
	 */
	public void run() {
		
		if (allDone) {
			return;
		}
		
		try {
			this.socketServer = new ServerSocket(this.port);
			this.listening = true;
			debug("listening");

			while (listening && !allDone) {
				Socket socket = this.socketServer.accept();
				debug("client connection from "
						+ socket.getRemoteSocketAddress());
				LobbyServerConnection socketConnection = new LobbyServerConnection(
						socket, this);
				socketConnection.start();
				this.clientConnections.add(socketConnection);
			}
			;
		} catch (Exception e) {
			debug(e.getMessage());
		}
	}

	/**
	 * Broadcasts a message to all the clients.
	 * 
	 * @param msg
	 *            the message to be sent
	 */
	public void writeToAll(String msg) {
		try {
			for (int i = 0; i < this.clientConnections.size(); i++) {
				LobbyServerConnection client = this.clientConnections.get(i);
				client.write(msg);
			}
			debug("broadcast message '" + msg + "' was sent");
		} catch (Exception e) {
			debug("Exception (writeToAll): " + e.getMessage());
		}
	}

	/**
	 * Removes a client from the server (it's expected that the client closes
	 * its own connection).
	 * 
	 * @param remoteAddress
	 *            the remote address of the client's socket connection
	 * @return true if the client was successfully removed
	 */
	public boolean remove(SocketAddress remoteAddress) {
		try {
			for (int i = 0; i < this.clientConnections.size(); i++) {
				LobbyServerConnection client = this.clientConnections.get(i);

				if (client.getRemoteAddress().equals(remoteAddress)) {
					this.clientConnections.remove(i);
					debug("client " + remoteAddress + " was removed");
					writeToAll(remoteAddress + " has disconnected.");

					return true;
				}
			}
		} catch (Exception e) {
			debug("Exception (remove): " + e.getMessage());
		}

		return false;
	}

	/**
	 * Closes the server's socket.
	 */
	protected void finalize() {
		try {
			if (this.socketServer != null) {
				this.socketServer.close();
			}
			this.listening = false;
			this.allDone = true;
			debug("stopped");
		} catch (Exception e) {
			debug("Exception (finalize): " + e.getMessage());
		}
	}
}
