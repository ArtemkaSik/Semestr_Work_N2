package network;

import config.ServerConfig;
import entity.Bullet;
import entity.Starship;
import network.packet.Packet;
import network.packet.connection.ConnectPacket;
import network.packet.connection.DisconnectPacket;
import network.packet.object.BulletPacket;
import network.packet.object.StarshipPacket;
import network.packet.Packet;
import network.types.Types;
import util.CollisionChecker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static network.types.Types.*;

public class GameClient implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final byte[] receiveData;
    private boolean running;
    private boolean connected;
    private final CollisionChecker collisionChecker;

    private final Starship localPlayer;
    private final Starship enemyPlayer;

    public GameClient(String serverIp, Starship localPlayer) throws IOException {
        System.out.println("Initializing client, connecting to server: " + serverIp);
        this.socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverIp);
        this.receiveData = new byte[ServerConfig.BUFFER_SIZE];
        this.enemyPlayer = new Starship();
        this.localPlayer = localPlayer;
        this.running = true;
        this.connected = false;
        this.collisionChecker = new CollisionChecker();

        // Отправляем пакет подключения
        sendConnectPacket();
    }

    private void sendConnectPacket() {
        try {
            System.out.println("Sending connect packet to server...");
            sendPacket(new ConnectPacket());
        } catch (IOException e) {
            System.err.println("Failed to send connect packet: " + e.getMessage());
            e.printStackTrace();
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
                    System.err.println("Error receiving packet: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePacket(DatagramPacket packet) {
        byte[] data = packet.getData();
        Types type = Types.values()[data[0]];
        String playerId = packet.getAddress().getHostAddress() + ":" + packet.getPort();

        switch (type) {
            case PLAYER_INFO -> {
                StarshipPacket posPacket = new StarshipPacket(data);
                updatePlayerPosition(playerId, posPacket);
            }
            case BULLET -> {
                BulletPacket bulletPacket = new BulletPacket(data);
                handleBulletData(playerId, bulletPacket);
            }
            case CONNECT_CONFIRM -> {
                connected = true;
                System.out.println("Connected to server successfully!");
            }
            case DISCONNECT -> {
                this.running = false;
                System.out.println("Enemy disconnected");
            }
        }
    }

    private void updatePlayerPosition(String playerId, StarshipPacket posPacket) {
        Starship player = otherPlayers.computeIfAbsent(playerId, k ->
                new Player(null, collisionChecker, posPacket.getName())
        );

        // Обновляем состояние игрока, включая номер спрайта
        player.updatePlayer(
                posPacket.getX(),
                posPacket.getY(),
                posPacket.getDirection(),
                posPacket.getSpriteNum(),
                posPacket.getHealth()
        );

        player.setName(posPacket.getName());
        player.updateState(posPacket.isDead());

        // Обновляем пули этого игрока с корректным CollisionChecker
        player.getBullets().forEach(bullet -> {
            bullet.update(collisionChecker);

            // Проверяем коллизии с локальным игроком
            if (bullet.isActive() && collisionChecker.checkBulletPlayerCollision(bullet, localPlayer.getWorldSolidArea())) {
                bullet.setActive(false);
                // Здесь можно добавить логику урона по игроку
                localPlayer.takeDamage(bullet.getDamage());
                System.out.println("Health: " + localPlayer.getHealth());
            }

            // Проверяем коллизии с другими игроками
            otherPlayers.forEach((otherId, otherPlayer) -> {
                if (!otherId.equals(playerId) && // Не проверяем владельца пули
                        bullet.isActive() &&
                        collisionChecker.checkBulletPlayerCollision(bullet, otherPlayer.getWorldSolidArea())) {
                    bullet.setActive(false);
                    // Здесь можно добавить логику урона по игроку
                }
            });
        });

        // Удаляем неактивные пули
        player.getBullets().removeIf(bullet -> !bullet.isActive());
    }

    public void sendPlayerPosition() {
        if (!connected) {
            System.out.println("Not connected to server, attempting to reconnect...");
            sendConnectPacket();
            return;
        }

        try {
            // Отправляем текущее состояние игрока, включая номер спрайта
            PlayerDataPacket packet = new PlayerDataPacket(
                    localPlayer.getWorldX(),
                    localPlayer.getWorldY(),
                    localPlayer.getDirection(),
                    localPlayer.getSpriteNum(), // Получаем текущий номер спрайта
                    localPlayer.isDead(),
                    localPlayer.getName(),
                    localPlayer.getHealth()
            );
            sendPacket(packet);

            // Отправляем данные о пулях и проверяем коллизии
            for (Bullet bullet : localPlayer.getBullets()) {
                if (bullet.isActive()) {
                    // Проверяем коллизии с другими игроками
                    otherPlayers.values().forEach(otherPlayer -> {
                        if (collisionChecker.checkBulletPlayerCollision(bullet, otherPlayer.getWorldSolidArea()) && !otherPlayer.isDead()) {
                            bullet.setActive(false);
                        }
                    });

                    if (bullet.isActive()) {
                        BulletDataPacket bulletPacket = new BulletDataPacket(
                                bullet.getWorldX(),
                                bullet.getWorldY(),
                                bullet.getDirection()
                        );
                        sendPacket(bulletPacket);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to send player position: " + e.getMessage());
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

    private void handleBulletData(String playerId, BulletPacket packet) {
        Starship starship = enemyPlayer.get(playerId);
        if (starship != null) {
            // Проверяем, нет ли уже такой пули
            boolean bulletExists = starship.getBullets().stream()
                    .anyMatch(b -> b.getWorldX() == packet.getX() &&
                            b.getWorldY() == packet.getY() &&
                            b.getDirection() == packet.getDirection());

            if (!bulletExists) {
                starship.addBullet(packet.getX(), packet.getY(), packet.getDirection());
            }
        }
    }
}