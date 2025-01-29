package network.packet;

import java.nio.ByteBuffer;

public class ShootPacket extends Packet {
    private int x;
    private int y;

    public ShootPacket() {
        super(SHOOT_PACKET);
    }

    public ShootPacket(int x, int y) {
        super(SHOOT_PACKET);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(9); // 1 byte type + 4 bytes x + 4 bytes y
        buffer.put(packetType);
        buffer.putInt(x);
        buffer.putInt(y);
        return buffer.array();
    }

    @Override
    public void deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Skip packet type
        x = buffer.getInt();
        y = buffer.getInt();
    }
} 