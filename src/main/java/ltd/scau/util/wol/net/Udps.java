package ltd.scau.util.wol.net;

import java.io.IOException;
import java.net.*;

/**
 * @author Wu Weijie
 */
public class Udps {

    public static void send(String targetHost, int targetPort, byte[] data) throws IOException {
        InetAddress targetAddress = InetAddress.getByName(targetHost);
        send(targetAddress, targetPort, data);
    }

    public static void send(InetAddress targetAddress, int targetPort, byte[] data) throws IOException {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, targetPort);
            datagramSocket.send(packet);
        }
    }
}
