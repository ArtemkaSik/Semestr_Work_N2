package network;

import network.packet.Packet;

public class NetworkManager {
    private GameServer server;
    private GameClient client;
    private final boolean isHost;

    public NetworkManager(boolean isHost) {
        this.isHost = isHost;
    }

    public void start(String remoteAddress) {
        if (isHost) {
            server = new GameServer();
            new Thread(() -> server.start()).start();
        } else {
            client = new GameClient();
            new Thread(() -> client.connect(remoteAddress)).start();
        }
    }

    public void stop() {
        if (isHost && server != null) {
            server.stop();
        } else if (!isHost && client != null) {
            client.disconnect();
        }
    }

    public void sendPacket(Packet packet) {
        if (isHost && server != null) {
            server.sendPacket(packet);
        } else if (!isHost && client != null) {
            client.sendPacket(packet);
        }
    }

    public Packet receivePacket() {
        if (isHost && server != null) {
            return server.receivePacket();
        } else if (!isHost && client != null) {
            return client.receivePacket();
        }
        return null;
    }

    public boolean isConnected() {
        if (isHost && server != null) {
            return server.isRunning();
        } else if (!isHost && client != null) {
            return client.isConnected();
        }
        return false;
    }
} 