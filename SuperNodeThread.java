import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.io.IOException;

public class SuperNodeThread extends Thread {

    private DatagramSocket socket;
    private InetAddress groupIP;
    private int groupPort;
    private int port;
    private byte[] resource = new byte[1024];
    private byte[] response = new byte[1024];
    Hashtable<String, Resource> distribuitedHashTable;
    private List<Integer> timeoutVal = new ArrayList<>();

    public SuperNodeThread(String[] args) throws IOException {
        groupIP = InetAddress.getByName(args[0]);
        groupPort = Integer.parseInt(args[1]);
        port = Integer.parseInt(args[2]);
        socket = new DatagramSocket(port);
    }

    public void run() {

        while (true) {
            try {

                DatagramPacket packet = new DatagramPacket(resource, resource.length);
                socket.setSoTimeout(500);
                socket.receive(packet);
                System.out.print("Recebi!");

                String content = new String(packet.getData(), 0, packet.getLength());
                InetAddress peerIP = packet.getAddress();
                int peerPort = packet.getPort();
                String[] vars = content.split("\\s");

                if (vars[0].equals("create") && vars.length > 1) {
                    // java P2P create 0x124421 0x43432 0x12123

                    for (int i = 1; i < vars.length; i++) {
                        Resource resource = new Resource(vars[i], "file-" + vars[i] + ".txt", peerIP, peerPort);
                        distribuitedHashTable.put(vars[i], resource);
                    }
                    response = "OK".getBytes();
                    packet = new DatagramPacket(response, response.length, peerIP, peerPort);
                    socket.send(packet);
                }

                if (vars[0].equals("find") && vars.length > 1) {
                    this.send(vars[1]);
                }

                if (vars[0].equals("heartbeat") && vars.length > 1) {
                    System.out.print("\nheartbeat: " + vars[1]);
                    for (int i = 0; i < distribuitedHashTable.keySet().size(); i++) {
                        if (distribuitedHashTable.keySet().toArray()[i].equals(vars[1]))
                            timeoutVal.set(i, 15);
                    }
                }
            } catch (IOException e) {
                // decrementa os contadores de timeout a cada 500ms (em função do receive com timeout)
                for (int i = 0; i < timeoutVal.size(); i++) {
                    timeoutVal.set(i, timeoutVal.get(i) - 1);
                    if (timeoutVal.get(i) == 0) {
                        System.out.println("\nuser " + resourceList.get(i) + " is dead.");
                        resourceList.remove(i);
                        resourceAddr.remove(i);
                        resourcePort.remove(i);
                        timeoutVal.remove(i);
                    }
                }
                System.out.print(".");
            }
        }
    }

    private void send(String message) {
        try {
            byte[] out = message.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(out, out.length, this.groupIP, this.groupPort);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
