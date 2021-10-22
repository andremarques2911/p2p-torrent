import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NodeThread extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress supernodeIP = null;
	protected int supernodePort;
	protected byte[] resource = new byte[1024];
	protected byte[] response = new byte[1024];
	protected int port;
	protected String[] vars;

	public NodeThread(String[] args) throws IOException {
		// envia um packet
		resource = args[1].getBytes();
		supernodeIP = InetAddress.getByName(args[0]);
		supernodePort = Integer.parseInt(args[1]);
		port = Integer.parseInt(args[3]);
		// cria um socket datagrama
		socket = new DatagramSocket(port);
		vars = args[2].split("\\s");
	}

	public void run() {
		
		try {
			// envia um packet
			DatagramPacket packet = new DatagramPacket(resource, resource.length, supernodeIP, supernodePort);
			socket.send(packet);
		} catch (IOException e) {
			socket.close();
		}
		
		while (true) {
			try {
				// obtem a resposta
				packet = new DatagramPacket(response, response.length);
				socket.setSoTimeout(500);
				socket.receive(packet);
				
				// mostra a resposta
				String data = new String(packet.getData(), 0, packet.getLength());
				System.out.println("recebido: " + data);
				
			} catch (IOException e) {
//				if (!vars[0].equals("wait")) {
//					// fecha o socket
//					socket.close();
//					break;
//				}
			}
		}

	}
}
