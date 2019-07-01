package ltd.scau.util.wol.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Wu Weijie
 */
public class Interfaces {

    /**
     * 扫描所有IPv4广播地址
     * @return 所有广播地址
     * @throws SocketException
     */
    public static List<InetAddress> getAllBroadcastAddresses() throws SocketException {
        List<String> strings = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        while (interfaces.hasMoreElements()) {
            networkInterfaces.add(interfaces.nextElement());
        }
        return networkInterfaces
                .stream()
                .flatMap(i -> i.getInterfaceAddresses().stream())
                .map(InterfaceAddress::getBroadcast)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static final String[] ARP_COMMAND = "arp -a".split(" ");

    public static final Pattern MAC_PATTERN = Pattern.compile(".*([0-9A-Fa-f]{2}(?:[-:][0-9A-Fa-f]{2}){5}).*");

    public static final String MAC_SPLIT_REGEX = "[:-]";

    /**
     * 扫描局域网中所有MAC地址
     *
     * @return
     * @throws IOException
     */
    public static Set<byte[]> scanMacAddresses() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(ARP_COMMAND);
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String s;
            while ((s = reader.readLine()) != null) {
                lines.add(s);
            }
        }
        return lines.stream()
                .map(MAC_PATTERN::matcher)
                .filter(Matcher::find)
                .map(m -> m.group(1))
                .map(a -> {
                    String[] splitAddress = a.split(MAC_SPLIT_REGEX);
                    byte[] addr = new byte[6];
                    for (int i = 0; i < splitAddress.length; i++) {
                        addr[i] = Integer.valueOf(splitAddress[i], 16).byteValue();
                    }
                    return addr;
                })
                .collect(Collectors.toSet());
    }

}
