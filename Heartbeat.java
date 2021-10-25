import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Heartbeat extends Thread {
	private final DatagramSocket socket;
	private final InetAddress address;
	private final int superNodePort;
	private final byte[] data;

	public Heartbeat(String[] args, DatagramSocket socket) throws IOException {
		data = ("heartbeat").getBytes();
		address = InetAddress.getByName(args[0]);
		superNodePort = Integer.parseInt(args[1]);
		this.socket = socket;
	}

	public void run() {
		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, superNodePort);
				socket.send(packet);
			} catch (IOException e) {
				socket.close();
			}
			try {
				Thread.sleep(5000);
			} catch(InterruptedException e) {
			}
		}
	}
}
