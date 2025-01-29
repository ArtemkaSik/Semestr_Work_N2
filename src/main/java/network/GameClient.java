package network;

import network.packet.Packet;
import java.io.*;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = false;
    private static final int PORT = 5000;
    private String serverAddress;

    public void connect(String serverAddress) {
        this.serverAddress = serverAddress;
        try {
            socket = new Socket(serverAddress, PORT);
            running = true;
            System.out.println("Connected to server: " + serverAddress);
            
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
        } catch (IOException e) {
            System.out.println("Failed to connect to server: " + e.getMessage());
            disconnect();
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error during disconnect: " + e.getMessage());
        }
        in = null;
        out = null;
        socket = null;
    }

    public void sendPacket(Packet packet) {
        if (!isConnected()) {
            tryReconnect();
            return;
        }

        try {
            if (out != null) {
                out.writeObject(packet);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Error sending packet: " + e.getMessage());
            disconnect();
            tryReconnect();
        }
    }

    public Packet receivePacket() {
        if (!isConnected()) {
            return null;
        }

        try {
            if (in != null && in.available() > 0) {
                return (Packet) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving packet: " + e.getMessage());
            disconnect();
            tryReconnect();
        }
        return null;
    }

    private void tryReconnect() {
        if (!running) return;
        
        System.out.println("Attempting to reconnect...");
        try {
            Thread.sleep(1000); // Подождем секунду перед переподключением
            connect(serverAddress);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isConnected() {
        return running && socket != null && socket.isConnected() && !socket.isClosed();
    }
} 