package ltd.scau.util.wol.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Wu Weijie
 */
public class WakeOnLan {

    public static final byte[] MAGIC_PACKET_PREFIX = new byte[]{
            (byte) 0xFF,
            (byte) 0xFF,
            (byte) 0xFF,
            (byte) 0xFF,
            (byte) 0xFF,
            (byte) 0xFF,
    };

    public static final int MAC_LENGTH = 6;

    public static final int MAGIC_PACKET_REPEAT_TIMES = 16;

    public static final int DATA_LENGTH = MAGIC_PACKET_PREFIX.length + MAC_LENGTH * MAGIC_PACKET_REPEAT_TIMES;

    /**
     * 构造唤醒魔包数据部分
     * @param mac 被唤醒对象MAC地址
     * @return 唤醒魔包
     */
    public static byte[] mac2wol(byte[] mac) {
        byte[] data = new byte[DATA_LENGTH];
        for (int i = 0; i < MAGIC_PACKET_PREFIX.length; i++) {
            data[i] = MAGIC_PACKET_PREFIX[i];
        }
        for (int i = MAGIC_PACKET_PREFIX.length, j = 0; i < data.length; i++, j = ++j % MAC_LENGTH) {
            data[i] = mac[j];
        }
        return data;
    }

    public static void wakeAll() throws IOException {
        List<InetAddress> allBroadcastAddresses = Interfaces.getAllBroadcastAddresses();
        List<byte[]> magicPackets = Interfaces.scanMacAddresses().stream().map(WakeOnLan::mac2wol).collect(Collectors.toList());
        for (InetAddress address : allBroadcastAddresses) {
            for (byte[] packet : magicPackets) {
                Udps.send(address, 23333, packet);
            }
        }
    }
}
