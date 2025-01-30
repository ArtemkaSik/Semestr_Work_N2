package network.packet.connection;

import network.packet.Packet;
import network.types.Types;

public class DisconnectPacket implements Packet {

    @Override
    public byte[] getData() {
        return new byte[] { (byte) Types.DISCONNECT.ordinal() };
    }
}
