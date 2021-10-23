import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SuperNodeThread extends Thread {

    private DatagramSocket socket;
    private InetAddress groupIP;
    private int groupPort;
    private int port;
    private byte[] resource = new byte[1024];
    private byte[] response = new byte[1024];
    private ConcurrentHashMap<String, Resource> distributedHashTable;
    private Hashtable<String, String> peerResources;
    private List<Peer> timeouts;

    public SuperNodeThread(String[] args, ConcurrentHashMap<String, Resource> distributedHashTable) throws IOException {
        this.groupIP = InetAddress.getByName(args[0]);
        this.groupPort = Integer.parseInt(args[1]);
        this.port = Integer.parseInt(args[2]);
        this.socket = new DatagramSocket(port);
        this.distributedHashTable = distributedHashTable;
        this.peerResources = new Hashtable<>();
        this.timeouts = new ArrayList<>();
    }

    public void run() {
        InetAddress peerIP = null;
        int peerPort = 0;
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(resource, resource.length);
                socket.setSoTimeout(500);
                socket.receive(packet);
                System.out.print("Recebi!");

                String content = new String(packet.getData(), 0, packet.getLength());
                peerIP = packet.getAddress();
                peerPort = packet.getPort();
                String[] vars = content.split("\\s");
                String key = peerIP + ":" + peerPort;

                if (vars[0].equals("create") && vars.length > 1) {
                    // java P2P create 0x124421 0x43432 0x12123
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < vars.length; i++) {
                        Resource resource = new Resource(vars[i], "file-" + vars[i] + ".txt", peerIP, peerPort);
                        distributedHashTable.put(vars[i], resource);
                        sb.append(vars[i]).append(" ");
                    }
                    peerResources.put(key, sb.toString());
                    response = "OK".getBytes();
                    packet = new DatagramPacket(response, response.length, peerIP, peerPort);
                    socket.send(packet);
                }

                if (vars[0].equals("find") && vars.length > 1) {
                    for (int i = 1; i < vars.length; i++) {
                       this.send(key + "-" + vars[i], this.groupIP, this.groupPort);
                    }
                }

                if (vars[0].equals("finded") && vars.length > 1) {
                    String[] address = vars[1].split(":");
                    InetAddress nodeIP = InetAddress.getByName(address[0]);
                    int nodePort = Integer.parseInt(address[1]);
                    String resourceAddress = vars[2];
                    String fileName = vars[3];

                    String res = resourceAddress + " " + fileName;
                    this.send(res, nodeIP, nodePort);
                }

                if (vars[0].equals("heartbeat") && vars.length > 1) {
                    System.out.print("\nheartbeat: " + vars[1]);
                    Peer peer = findPeer(peerIP, peerPort);
                    if (peer != null)
                        peer.setTimeout(15);
                }
            } catch (IOException e) {
                // decrementa os contadores de timeout a cada 500ms (em função do receive com timeout)
                Peer peer = findPeer(peerIP, peerPort);
                peer.setTimeout(peer.getTimeout() -1);
                if (peer.getTimeout() == 0) {
                    System.out.println("\nPeer " + peer.getIp() + ":" + peer.getPort() + " is dead.");
                    InetAddress finalPeerIP = peerIP;
                    int finalPeerPort = peerPort;
                    String key = finalPeerIP + ":" + finalPeerPort;
                    String value = peerResources.get(key);
                    if (value != null) {
                        String[] resources = value.split("");
                        for (String r : resources) {
                            distributedHashTable.remove(r);
                        }
                        peerResources.remove(key);
                    }
                    timeouts.removeIf(p -> p.getIp().equalsIgnoreCase(finalPeerIP.getHostAddress()) && p.getPort() == finalPeerPort);
                }
                System.out.print(".");
            }
        }
    }

    private Peer findPeer(InetAddress peerIP, int peerPort) {
        return timeouts.stream()
                .filter(p -> p.getIp().equalsIgnoreCase(peerIP.getHostAddress()) && p.getPort() == peerPort)
                .findFirst()
                .orElseGet(null);
    }
    
    private void send(String message, InetAddress ip, int port) throws IOException {
        byte[] out = message.getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(out, out.length, ip, port);
        socket.send(packet);
        socket.close();
    }
}
