package network;

import config.NetworkConfig;
import config.ServerConfig;
import network.packet.Packet;
import network.packet.impl.DisconnectPacket;
import network.packet.impl.BonusPacket;
import network.packet.enums.PacketType;
import manager.BonusManager;
import network.types.Types;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

public class GameServer implements Runnable {
    private final DatagramSocket socket;
    private final byte[] receiveData;
    private final ConcurrentHashMap<String, ClientInfo> clients;
    private boolean running;

    public GameServer() throws IOException {
        System.out.println("Starting server on port " + ServerConfig.SERVER_PORT);
        this.socket = new DatagramSocket(ServerConfig.SERVER_PORT);
        this.receiveData = new byte[ServerConfig.BUFFER_SIZE];
        this.clients = new ConcurrentHashMap<>();
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Server is running...");
        while (running) {
            try {
                Arrays.fill(receiveData, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );
                socket.receive(receivePacket);

                // Обработка входящего пакета
                handlePacket(receivePacket);

            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
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
                e.printStackTrace();
            }
        }
    }

    private void handlePacket(DatagramPacket packet) throws IOException {
        byte[] data = packet.getData();
        Types type = Types.values()[data[0]];
        String clientId = packet.getAddress().getHostAddress() + ":" + packet.getPort();

        switch (type) {
            case BULLET, PLAYER_INFO -> {
                broadcastToOtherClients(packet, clientId);
            }
            case CONNECT -> {
                // Добавляем нового клиента
                ClientInfo clientInfo = new ClientInfo(packet.getAddress(), packet.getPort());
                clients.put(clientId, clientInfo);
                System.out.println("New client connected: " + clientId);
                // Отправляем подтверждение подключения
                sendConnectionConfirmation(packet.getAddress(), packet.getPort());

            }
            case DISCONNECT -> {
                clients.remove(clientId);
                System.out.println("Client disconnected: " + clientId);
                // Отправляем пакет отключения всем клиентам
                broadcastPacket(new DisconnectPacket());
            }
        }
    }

    private void sendConnectionConfirmation(InetAddress address, int port) throws IOException {
        byte[] confirmData = new byte[]{(byte) Types.CONNECT_CONFIRM.ordinal()};
        DatagramPacket confirmPacket = new DatagramPacket(
                confirmData,
                confirmData.length,
                address,
                port
        );
        socket.send(confirmPacket);
        System.out.println("Sent connection confirmation to " + address + ":" + port);
    }

    private void broadcastToOtherClients(DatagramPacket sourcePacket, String sourceClientId) throws IOException {
        for (var entry : clients.entrySet()) {
            if (!entry.getKey().equals(sourceClientId)) {
                ClientInfo client = entry.getValue();
                DatagramPacket broadcastPacket = new DatagramPacket(
                        sourcePacket.getData(),
                        sourcePacket.getLength(),
                        client.address(),
                        client.port()
                );
                socket.send(broadcastPacket);
                //System.out.println("Broadcasting to client: " + entry.getKey());
            }
        }
    }

    public boolean hasClients() {
        return !clients.isEmpty();
    }

    public void stop() {
        running = false;
        socket.close();
        System.out.println("Server stopped");
    }

    private record ClientInfo(InetAddress address, int port) {}
}