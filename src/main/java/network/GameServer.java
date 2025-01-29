package network;

import network.packet.Packet;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = false;
    private static final int PORT = 5000;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Server started on port " + PORT);
            
            while (running) {
                try {
                    // Ждём подключения клиента
                    clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    
                    out = new ObjectOutputStream(clientSocket.getOutputStream());
                    in = new ObjectInputStream(clientSocket.getInputStream());
                    
                } catch (IOException e) {
                    System.out.println("Error accepting client: " + e.getMessage());
                    cleanup();
                }
            }
            
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            stop();
        }
    }

    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error during cleanup: " + e.getMessage());
        }
        in = null;
        out = null;
        clientSocket = null;
    }

    public void stop() {
        running = false;
        cleanup();
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }

    public void sendPacket(Packet packet) {
        if (!isRunning()) {
            return;
        }

        try {
            if (out != null) {
                out.writeObject(packet);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Error sending packet: " + e.getMessage());
            cleanup();
        }
    }

    public Packet receivePacket() {
        if (!isRunning()) {
            return null;
        }

        try {
            if (in != null && in.available() > 0) {
                return (Packet) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving packet: " + e.getMessage());
            cleanup();
        }
        return null;
    }

    public boolean isRunning() {
        return running && clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }
} 