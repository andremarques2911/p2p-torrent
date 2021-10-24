import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SuperNodeGroupReceiver extends Thread {
    
    private MulticastSocket socket;
    private InetAddress inetAddress;
    private String groupIP;
    private int groupPort;
    ConcurrentHashMap<String, Resource> distributedHashTable;

    public SuperNodeGroupReceiver(String[] args, ConcurrentHashMap<String, Resource> distributedHashTable) {
        this.groupIP = args[0];
        this.groupPort = Integer.parseInt(args[1]);
        this.distributedHashTable = distributedHashTable;
    }

    @Override
    public void run() {
        try {
            this.socket = new MulticastSocket(this.groupPort);
            this.inetAddress = InetAddress.getByName(this.groupIP);
            this.socket.joinGroup(inetAddress);

            while (true) {

                byte[] in = new byte[256];
                DatagramPacket packet = new DatagramPacket(in, in.length);

                this.socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);
//                "192.168.10.16:5000-gasdasdhashjd"
                String[] data = received.split("-");
                String address = data[0];
                String hash = data[1];

                Resource resource = distributedHashTable.get(hash);
                if (resource != null) {
                    String sb = "found " +
                            address + " " +
                            resource.getIp() + ":" + resource.getPort() + " " +
                            resource.getFileName() + " ";
                    byte[] dataRes = sb.getBytes();
                    DatagramPacket res = new DatagramPacket(dataRes, dataRes.length, packet.getAddress(), packet.getPort());
                    socket.send(res);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                if (Objects.nonNull(this.socket)) {
                    this.socket.leaveGroup(inetAddress);
                    this.socket.close();
                }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
