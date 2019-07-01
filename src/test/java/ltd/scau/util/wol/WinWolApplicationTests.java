package ltd.scau.util.wol;

import ltd.scau.util.wol.net.WakeOnLan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WinWolApplicationTests {

    private static final String BROADCAST_HOST = "192.168.101.255";
    private static final int DPORT = 23333;

    private static final byte[] DATA = "hello, world".getBytes();

    @Test
    public void udpTest() throws Exception {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(BROADCAST_HOST);
            DatagramPacket packet = new DatagramPacket(DATA, DATA.length, address, DPORT);
            datagramSocket.send(packet);
        }
    }

    @Test
    public void getAddress() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        while (interfaces.hasMoreElements()) {
            networkInterfaces.add(interfaces.nextElement());
        }
        List<InetAddress> broadcastAddresses = networkInterfaces
                .stream()
                .flatMap(i -> i.getInterfaceAddresses().stream())
                .map(InterfaceAddress::getBroadcast)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        System.out.println(broadcastAddresses);
    }

    private static final String[] WIN_COMMAND = "arp -a".split(" ");

    private static final Pattern MAC_PATTERN = Pattern.compile(".*([0-9A-Fa-f]{2}(?:-[0-9A-Fa-f]{2}){5}).*");

    @Test
    public void testSystemCommand() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(WIN_COMMAND);
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
//        process.waitFor();
//        System.out.println("Exit: " + process.exitValue());
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String s;
            while ((s = reader.readLine()) != null) {
                lines.add(s);
            }
        }
        List<byte[]> macs = lines.stream()
                .map(MAC_PATTERN::matcher)
                .filter(Matcher::find)
                .map(m -> m.group(1))
                .map(a -> {
                    String[] splitAddress = a.split("-");
                    byte[] addr = new byte[6];
                    for (int i = 0; i < splitAddress.length; i++) {
                        addr[i] = Integer.valueOf(splitAddress[i], 16).byteValue();
                    }
                    return addr;
                })
                .collect(Collectors.toList());
        macs.forEach(b -> {
            for (byte a : b) {
                System.out.printf("%02x ", a);
            }
            System.out.println();
        });
    }

    @Test
    public void wakeAllUp() throws Exception {
        WakeOnLan.wakeAll();
    }
}
