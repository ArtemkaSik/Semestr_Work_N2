package network;

import config.ServerConfig;
import network.packet.Packet;
import network.packet.connection.DisconnectPacket;
import network.types.Types;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

public class GameServer implements Runnable {

    private final byte[] data;
    private final DatagramSocket socket;
    private final ConcurrentHashMap<String, ClientInfo> clients;
    private boolean running;

    public GameServer() throws IOException {
        this.data = new byte[ServerConfig.BUFFER_SIZE];
        this.socket = new DatagramSocket(ServerConfig.SERVER_PORT);
        this.clients = new ConcurrentHashMap<>();
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Server is running...");
        while (running) {
            try {
                Arrays.fill(data, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(
                        data,
                        data.length
                );
                socket.receive(receivePacket);

                // Обработка входящего пакета
                handlePacket(receivePacket);

            } catch (IOException e) {
                System.out.println("Server running error: " + e);
            }
        }
    }

    public void broadcastPacket(Packet packet) {
        for (ClientInfo client : clients.values()) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(
                        packet.getData(),
                        packet.getData().length,
                        client.address(),
                        client.port()
                );
                socket.send(datagramPacket);
            } catch (IOException e) {
                System.out.println("The Error when data packing: " + e);
            }
        }
    }

    private void handlePacket(DatagramPacket packet) throws IOException {
        byte[] data = packet.getData();
        Types type = Types.values()[data[0]];
        String clientId = packet.getAddress().getHostAddress() + ":" + packet.getPort();

        switch (type) {
            case PLAYER_INFO, BULLET -> {
                broadcastToClients(packet, clientId);
            }
            case CONNECT -> {
                ClientInfo clientInfo = new ClientInfo(packet.getAddress(), packet.getPort());
                clients.put(clientId, clientInfo);
                System.out.println("Player connected: " + clientId);
                sendConnection(packet.getAddress(), packet.getPort());
            }
            case DISCONNECT -> {
                clients.remove(clientId);
                System.out.println("Client disconnected: " + clientId);
                broadcastPacket(new DisconnectPacket());
            }
        }
    }

    private void sendConnection(InetAddress address, int port) throws IOException {
        byte[] confirmData = new byte[]{(byte) Types.CONNECT.ordinal()};
        DatagramPacket confirmPacket = new DatagramPacket(
                confirmData,
                confirmData.length,
                address,
                port
        );
        socket.send(confirmPacket);
    }

    private void broadcastToClients(DatagramPacket packet, String sourceClientId) throws IOException {
        for (var entry : clients.entrySet()) {
            if (!entry.getKey().equals(sourceClientId)) {
                ClientInfo client = entry.getValue();
                DatagramPacket broadcastPacket = new DatagramPacket(
                        packet.getData(),
                        packet.getLength(),
                        client.address(),
                        client.port()
                );
                socket.send(broadcastPacket);
            }
        }
    }

    private record ClientInfo(InetAddress address, int port) {}
}