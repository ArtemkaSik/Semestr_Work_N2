package network.packet.connection;

import network.packet.Packet;
import network.types.Types;

public class ConnectConfirmPacket implements Packet {
    @Override
    public byte[] getData() {
        return new byte[] { (byte) Types.CONNECT.ordinal() };
    }
}
