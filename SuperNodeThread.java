import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SuperNodeThread extends Thread {
    private final DatagramSocket socket;
    private final InetAddress groupIP;
    private final int groupPort;
    private final byte[] resource = new byte[1024];
    private final ConcurrentHashMap<String, Resource> distributedHashTable;
    private final Hashtable<String, String> peerResources;
    private List<Peer> timeouts;

    public SuperNodeThread(String[] args, ConcurrentHashMap<String, Resource> distributedHashTable) throws IOException {
        this.groupIP = InetAddress.getByName(args[0]);
        this.groupPort = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[2]);
        this.socket = new DatagramSocket(port);
        this.distributedHashTable = distributedHashTable;
        this.peerResources = new Hashtable<>();
        this.timeouts = new CopyOnWriteArrayList<>();
    }

    public void run() {
        InetAddress peerIP = null;
        int peerPort = 0;
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(resource, resource.length);
                socket.setSoTimeout(500);
                socket.receive(packet);

                String content = new String(packet.getData(), 0, packet.getLength());
                peerIP = packet.getAddress();
                peerPort = packet.getPort();
                String[] vars = content.split("\\s");
                String key = peerIP + ":" + peerPort;

                if (vars[0].equals("create") && vars.length > 1) {
                    System.out.println("Creating new node with address " + peerIP + ":" + peerPort);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < vars.length; i++) {
                        Resource resource = new Resource(vars[i], "file-" + vars[i] + ".txt", peerIP, peerPort);
                        distributedHashTable.put(vars[i], resource);
                        sb.append(vars[i]).append(" ");
                    }
                    String value = sb.toString();
                    peerResources.put(key, value);
                    timeouts.add(new Peer(peerIP, peerPort, 20));
                    byte[] response = "OK".getBytes();
                    packet = new DatagramPacket(response, response.length, peerIP, peerPort);
                    socket.send(packet);
                }

                if (vars[0].equals("find") && vars.length > 1) {
                    System.out.println("Finding resources...");
                    for (int i = 1; i < vars.length; i++) {
                       this.send(key + "-" + vars[i], this.groupIP, this.groupPort);
                    }
                }

                if (vars[0].equals("found") && vars.length > 1) {
                    System.out.println("Found resources!");
                    String[] address = vars[1].split(":");
                    InetAddress nodeIP = InetAddress.getByName(address[0].replace("/", ""));
                    int nodePort = Integer.parseInt(address[1]);
                    String[] resourceAddress = vars[2].split(":");
                    String resourceIP = resourceAddress[0].replace("/", "");
                    String resourcePort = resourceAddress[1];
                    String fileName = vars[3];
                    String res = "peer " + fileName + " " + resourceIP + " " + resourcePort;
                    this.send(res, nodeIP, nodePort);
                }

                if (vars[0].equals("heartbeat")) {
                    Peer peer = findPeer(peerIP, peerPort);
                    System.out.println("Heartbeated by: " + peerIP + ": " + peerPort);
                    if (peer != null) {
                        peer.setTimeout(20);
                    }
                }
            } catch (IOException e) {
                for (Peer peer : timeouts) {
                    peer.setTimeout(peer.getTimeout() - 1);
                    if (peer.getTimeout() == 0) {
                        killPeer(peerIP, peerPort, peer);
                    }
                }
            }
        }
    }

    private void killPeer(InetAddress peerIP, int peerPort, Peer peer) {
        System.out.println("Peer " + peer.getIp() + ":" + peer.getPort() + " is dead.");
        String key = peerIP + ":" + peerPort;
        String value = peerResources.get(key);
        if (value != null) {
            String[] resources = value.split(" ");
            for (String r : resources) {
                distributedHashTable.remove(r);
            }
            peerResources.remove(key);
        }
        timeouts.removeIf(p -> p.getIp().equals(peerIP) && p.getPort() == peerPort);
    }

    private Peer findPeer(InetAddress peerIP, int peerPort) {
        for (Peer peer: timeouts) {
            if (peer.getIp().equals(peerIP) && peer.getPort() == peerPort) {
                return peer;
            }
        }
        return null;
    }

    private void send(String message, InetAddress ip, int port) throws IOException {
        System.out.println("Message: " + message + " " + ip + ": " + port);
        byte[] out = message.getBytes();
        DatagramPacket packet = new DatagramPacket(out, out.length, ip, port);
        socket.send(packet);
    }
}
