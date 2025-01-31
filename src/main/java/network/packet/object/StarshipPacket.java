package network.packet.object;

import lombok.Getter;
import lombok.Setter;
import network.packet.Packet;
import network.types.Types;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
public class StarshipPacket implements Packet {
    private final int x;
    private final int y;
    private boolean isAlive;
    private final int health;
    private boolean isHost;

    public StarshipPacket(int x, int y, boolean isAlive, int health, boolean isHost) {
        this.x = x;
        this.y = y;
        this.isAlive = isAlive;
        this.health = health;
        this.isHost = isHost;
    }

    public StarshipPacket(int x, int y, boolean isAlive, int health) {
        this.x = x;
        this.y = y;
        this.isAlive = isAlive;
        this.health = health;
    }

    public StarshipPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Skip packet type

        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.isAlive = buffer.get() == 1;
        this.health = buffer.getInt();
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(14);

        buffer.put((byte) Types.PLAYER_INFO.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) (isAlive ? 1 : 0));
        buffer.putInt(health);

        return buffer.array();
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive){
        this.isAlive = isAlive;
    }

    public boolean getIsHost(){
        return this.isHost;
    }
}
