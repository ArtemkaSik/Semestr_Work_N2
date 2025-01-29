package network.packet;

import java.nio.ByteBuffer;

public class ConnectPacket extends Packet {
    private boolean isHost;

    public ConnectPacket() {
        super(CONNECT_PACKET);
    }

    public ConnectPacket(boolean isHost) {
        super(CONNECT_PACKET);
        this.isHost = isHost;
    }

    public boolean isHost() {
        return isHost;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(2); // 1 byte for type, 1 byte for isHost
        buffer.put(packetType);
        buffer.put((byte) (isHost ? 1 : 0));
        return buffer.array();
    }

    @Override
    public void deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Skip packet type
        isHost = buffer.get() == 1;
    }
} 