import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class NodeThread extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress supernodeIP = null;
	protected int supernodePort;
	protected byte[] resource = new byte[1024];
	protected byte[] response = new byte[1024];
	protected int port;
	protected String hashes;

	public NodeThread(String[] args) throws IOException {
		supernodeIP = InetAddress.getByName(args[0]);
		supernodePort = Integer.parseInt(args[1]);
		port = Integer.parseInt(args[3]);
		socket = new DatagramSocket(port);
		hashes = args[2];
	}

	public void run() {
		try {
			// envia um packet
			resource = ("create " + hashes).getBytes();
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

				byte[] file = Files.readAllBytes(Path.of("./files/" + data + ".txt"));
				DatagramPacket filePacket = new DatagramPacket(file, file.length, packet.getAddress(), packet.getPort());
				socket.send(filePacket);
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
