import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Objects;

public class SuperNodeGroupReceiver extends Thread {
    protected MulticastSocket socket;
    protected InetAddress inetAddress;
    protected String groupIP;
    protected int groupPort;

    public SuperNodeGroupReceiver(String[] args) {
        this.groupIP = args[0];
        this.groupPort = Integer.parseInt(args[1]);
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
//                this.socket.setSoTimeout(500);
                this.socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);
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
