package network;

import config.ServerConfig;
import entity.Bullet;
import entity.Starship;
import network.packet.Packet;
import network.packet.connection.ConnectPacket;
import network.packet.connection.DisconnectPacket;
import network.packet.object.BulletPacket;
import network.packet.object.StarshipPacket;
import network.types.Types;
import util.CollisionChecker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class GameClient implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final byte[] receiveData;
    private boolean running;
    private boolean connected;
    private boolean bothPlayersConnected;
//    private final CollisionChecker collisionChecker;

    private final Starship localPlayer;
    private final Starship enemyPlayer;

    public GameClient(String serverIp, Starship localPlayer, Starship enemyPlayer) throws IOException {
        this.socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverIp);
        this.receiveData = new byte[ServerConfig.BUFFER_SIZE];
        this.enemyPlayer = enemyPlayer;
        this.localPlayer = localPlayer;
        this.running = true;
        this.connected = false;
        this.bothPlayersConnected = false;
//        this.collisionChecker = new CollisionChecker();

        // Отправляем пакет подключения
        sendConnectPacket();
    }

    private void sendConnectPacket() {
        try {
            System.out.println("Sending connect packet to server...");
            sendPacket(new ConnectPacket());
        } catch (IOException e) {
            System.out.println("Failed to send connect packet: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Client is running...");
        while (running) {
            try {
                Arrays.fill(receiveData, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );
                socket.receive(receivePacket);
                handlePacket(receivePacket);
            } catch (IOException e) {
                if (running) {
                    System.out.println("Error receiving packet: " + e.getMessage());
                }
            }
        }
    }

    private void handlePacket(DatagramPacket packet) {
        byte[] data = packet.getData();
        Types type = Types.values()[data[0]];

        switch (type) {
            case PLAYER_INFO -> {
                StarshipPacket posPacket = new StarshipPacket(data);
                updatePlayerPosition(posPacket);
            }
            case BULLET -> {
                BulletPacket bulletPacket = new BulletPacket(data);
                handleBulletData(bulletPacket);
            }
            case CONNECT -> {
                connected = true;
                System.out.println("Connected to server successfully!");
            }
            case DISCONNECT -> {
                connected = false;
                bothPlayersConnected = false;
                System.out.println("Enemy disconnected");
            }
            case GAME_START -> {
                bothPlayersConnected = true;
                System.out.println("Both players connected! Game starting...");
            }
        }
    }

    private void updatePlayerPosition(StarshipPacket posPacket) {
        // Обновляем состояние вражеского корабля
        enemyPlayer.updatePlayer(
                posPacket.getX(),
                posPacket.getY(),
                posPacket.getHealth()
        );

        // Обновляем пули врага
        enemyPlayer.getBullets().forEach(bullet -> {
            bullet.update(enemyPlayer.getIsHost());

            // Проверяем попадание во врага
//            if (bullet.isActive() && collisionChecker.checkBulletPlayerCollision(bullet, localPlayer)) {
//                bullet.notActive();
//                localPlayer.takeDamage();
//                System.out.println("Health: " + localPlayer.getCurrentHp());
//            }
        });

        // Удаляем неактивные пули
        enemyPlayer.getBullets().removeIf(bullet -> !bullet.isActive());
    }

    public void sendPlayerPosition() {
        if (!connected) {
            System.out.println("Not connected to server, attempting to reconnect...");
            sendConnectPacket();
            return;
        }

        try {
            // Отправляем текущее состояние игрока
            StarshipPacket packet = new StarshipPacket(
                    localPlayer.getX(),
                    localPlayer.getY(),
                    localPlayer.isAlive(),
                    localPlayer.getCurrentHp()
            );
            sendPacket(packet);

            // Отправляем данные о пулях
            for (Bullet bullet : localPlayer.getBullets()) {
                if (bullet.isActive()) {
                    BulletPacket bulletPacket = new BulletPacket(
                            bullet.getX(),
                            bullet.getY()
                    );
                    sendPacket(bulletPacket);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to send player position: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendPacket(Packet packet) throws IOException {
        byte[] data = packet.getData();
        DatagramPacket sendPacket = new DatagramPacket(
                data,
                data.length,
                serverAddress,
                ServerConfig.SERVER_PORT
        );
        socket.send(sendPacket);
    }

    public Starship getEnemy() {
        return enemyPlayer;
    }

    private void handleBulletData(BulletPacket packet) {
        boolean bulletExists = enemyPlayer.getBullets().stream()
                .anyMatch(b -> b.getX() == packet.getX() &&
                        b.getY() == packet.getY());

        if (!bulletExists) {
            enemyPlayer.getBullets().add(new Bullet(packet.getX(), packet.getY()));
        }
    }

    public boolean areBothPlayersConnected() {
        return bothPlayersConnected;
    }
}