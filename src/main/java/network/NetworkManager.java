package network;

import config.ServerConfig;
import network.packet.*;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkManager {
    private DatagramSocket socket;
    private InetAddress remoteAddress;
    private int remotePort;
    private boolean isHost;
    private boolean isConnected;
    private final BlockingQueue<Packet> incomingPackets;
    private Thread receiveThread;
    private volatile boolean running;

    public NetworkManager(boolean isHost) {
        this.isHost = isHost;
        this.incomingPackets = new LinkedBlockingQueue<>();
        this.running = true;
    }

    public void start(String remoteHost) throws IOException {
        if (isHost) {
            socket = new DatagramSocket(ServerConfig.SERVER_PORT);
        } else {
            socket = new DatagramSocket();
            remoteAddress = InetAddress.getByName(remoteHost);
            remotePort = ServerConfig.SERVER_PORT;
        }

        startReceiving();
    }

    private void startReceiving() {
        receiveThread = new Thread(() -> {
            byte[] buffer = new byte[ServerConfig.BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (running) {
                try {
                    socket.receive(packet);
                    
                    if (!isConnected) {
                        remoteAddress = packet.getAddress();
                        remotePort = packet.getPort();
                    }

                    Packet receivedPacket = deserializePacket(packet.getData());
                    if (receivedPacket != null) {
                        incomingPackets.offer(receivedPacket);
                        
                        if (receivedPacket instanceof ConnectPacket) {
                            handleConnectPacket((ConnectPacket) receivedPacket);
                        }
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        e.printStackTrace();
                    }
                }
            }
        });
        receiveThread.start();
    }

    private void handleConnectPacket(ConnectPacket packet) {
        if (!isConnected) {
            isConnected = true;
            // Send connection acknowledgment back
            sendPacket(new ConnectPacket(isHost));
        }
    }

    public void sendPacket(Packet packet) {
        if (remoteAddress == null || remotePort == 0) {
            return;
        }

        byte[] data = packet.serialize();
        DatagramPacket datagramPacket = new DatagramPacket(
            data,
            data.length,
            remoteAddress,
            remotePort
        );

        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Packet receivePacket() {
        return incomingPackets.poll();
    }

    private Packet deserializePacket(byte[] data) {
        if (data.length == 0) return null;

        Packet packet = null;
        byte packetType = data[0];

        switch (packetType) {
            case Packet.CONNECT_PACKET:
                packet = new ConnectPacket();
                break;
            case Packet.POSITION_PACKET:
                packet = new PositionPacket();
                break;
            case Packet.SHOOT_PACKET:
                packet = new ShootPacket();
                break;
            case Packet.DISCONNECT_PACKET:
                // Handle disconnect packet
                break;
        }

        if (packet != null) {
            packet.deserialize(data);
        }

        return packet;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
    }
} 