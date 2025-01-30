package network.packet.object;

import lombok.Getter;
import network.packet.Packet;
import network.types.Types;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
public class StarshipPacket implements Packet {
    private final int x;
    private final int y;
    private final int spriteNum;
    private final boolean isAlive;
    private final String name;
    private final int health;

    public StarshipPacket(int x, int y, int spriteNum, boolean isAlive, String name, int health) {
        this.x = x;
        this.y = y;
        this.spriteNum = spriteNum;
        this.isAlive = isAlive;
        this.name = name;
        this.health = health;
    }

    public StarshipPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get();

        //Получаем данные о корабле
        this.isAlive = buffer.get() == 1;
        this.spriteNum = buffer.get();
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.health = buffer.getInt();

        //Получаем имя
        byte[] nameBytes = new byte[buffer.get()];
        buffer.get(nameBytes);
        this.name = new String(nameBytes, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] getData() {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(1 + 3 + 3 + 1 + 1 + 1 + 1 + nameBytes.length + 4);

        buffer.put((byte) Types.PLAYER_INFO.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) spriteNum);
        buffer.put((byte) (isAlive ? 1 : 0));
        buffer.put((byte) nameBytes.length);
        buffer.put(nameBytes);
        buffer.putInt(health);

        return buffer.array();
    }

    public boolean isAlive() {
        return isAlive;
    }
}
