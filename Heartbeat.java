import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Heartbeat extends Thread {
	private DatagramSocket socket;
	private DatagramPacket packet = null;
	private InetAddress addr;
	private int superNodePort;
	private byte[] data;

	public Heartbeat(String[] args, DatagramSocket socket) throws IOException {
		data = ("heartbeat").getBytes();
		addr = InetAddress.getByName(args[0]);
		superNodePort = Integer.parseInt(args[1]);
		this.socket = socket;
	}

	public void run() {
		while (true) {
			try {
				packet = new DatagramPacket(data, data.length, addr, superNodePort);
				socket.send(packet);
			} catch (IOException e) {
				socket.close();
			}
			try {
				Thread.sleep(5000);
			} catch(InterruptedException e) {
			}
//			System.out.println("\npulse!");
		}
	}
}
