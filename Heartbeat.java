import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Heartbeat extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress addr = null;
	protected byte[] data = new byte[1024];
	protected int port;

	public Heartbeat(String[] args) throws IOException {
		// envia um packet
		String vars[] = args[2].split("\\s");
		data = ("heartbeat " + vars[1]).getBytes();
		addr = InetAddress.getByName(args[0]);
		port = Integer.parseInt(args[3]) + 100;
		// cria um socket datagrama
		socket = new DatagramSocket(port);
	}

	public void run() {
		while (true) {
			try {
				packet = new DatagramPacket(data, data.length, addr, 9000);
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
