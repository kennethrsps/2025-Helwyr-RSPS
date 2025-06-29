package com.rs.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.rs.Settings;
import com.rs.utils.Logger;

/**
 * Class that communicates with login server
 * @author Zeus
 */
public class LoginServerCommunication extends Thread {

    private static final Object LOCK = new Object();

    public LoginServerCommunication(String ip, int port) throws IOException {
		Logger.log(this, "Connecting to login server (" + ip + ":" + port + ").");
		this.socket = new Socket(ip, port);
		LoginServerCommunication.outStream = new ObjectOutputStream(socket.getOutputStream());
		this.inStream = new ObjectInputStream(socket.getInputStream());
		this.packetToSend = new LinkedList<Integer>();
		this.start();
		Logger.log(this, "Connected to login server (" + ip + ":" + port + ").");
    }

    public void run() {
		sendPacket(0);
		try {
		    while (true) {
			if (inStream.available() != 0)
			    read();
			if (packetToSend.peek() != null)
			    write();
		    }
		} catch (Exception e) {
		    Logger.handle(e);
		}
    }

    public void read() throws IOException, ClassNotFoundException {
		int packetType = inStream.readInt();
		switch (packetType) {
		case 0:
		    Object username = inStream.readObject();
		    recentCheck = username;
		    break;
		}
    }

    public void write() throws IOException {
		switch (packetToSend.poll()) {
		// case 0:
		// sendWorldDetails();
		// break;
		}
    }

    public void sendPacket(int id) {
		synchronized (packetToSend) {
		    packetToSend.add(id);
		}
    }

    public void sendWorldDetails() throws IOException {
		outStream.writeInt(0);
		outStream.writeObject("Main Server");
		outStream.writeInt(Settings.SERVER_PORT == 43595 ? 1 : 2);
		outStream.flush();
    }

    public static void sendPlayerLoggedIn(String username) throws IOException {
		String object = username;
		outStream.writeInt(1);
		outStream.writeObject(object);
		outStream.flush();
    }

    public static void sendPlayerLoggedOut(String username) throws IOException {
		String object = username;
		outStream.writeInt(3);
		outStream.writeObject(object);
		outStream.flush();
    }

    public static void checkPlayerOnlineStatus(String username) throws IOException {
		String object = username;
		System.out.println(object);
		outStream.writeInt(2);
		outStream.writeObject(object);
		outStream.flush();
    }

    public Socket socket;
    public static ObjectOutputStream outStream;
    public ObjectInputStream inStream;

    private Queue<Integer> packetToSend;

    public static Object recentCheck = null;

    public static boolean isOnline(String username) throws IOException {
		synchronized (LOCK) {
		    checkPlayerOnlineStatus(username);
		    String temp1 = (String) recentCheck;
		    if (username.equalsIgnoreCase((String) temp1)) {
			System.out.println("[LoginServerCommunication] According to the Login Server, "
					+ temp1 + " is already logged into World 1.");
			return true;
		    }
		    return false;
		}
    }
}