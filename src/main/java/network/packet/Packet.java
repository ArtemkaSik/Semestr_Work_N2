package network.packet;

import java.nio.ByteBuffer;

public abstract class Packet {
    public static final byte CONNECT_PACKET = 1;
    public static final byte POSITION_PACKET = 2;
    public static final byte SHOOT_PACKET = 3;
    public static final byte DISCONNECT_PACKET = 4;

    protected byte packetType;

    public Packet(byte packetType) {
        this.packetType = packetType;
    }

    public byte getPacketType() {
        return packetType;
    }

    public abstract byte[] serialize();
    public abstract void deserialize(byte[] data);
} 