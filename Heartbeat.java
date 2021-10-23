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
	private int port;

	public Heartbeat(String[] args) throws IOException {
		// envia um packet
		data = ("heartbeat").getBytes();
		addr = InetAddress.getByName(args[0]);
		superNodePort = Integer.parseInt(args[1]);
		port = Integer.parseInt(args[3]) + 100;
		// cria um socket datagrama
		socket = new DatagramSocket(port);
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
