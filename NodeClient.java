import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class NodeClient extends Thread {
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	protected InetAddress supernodeIP = null;
	protected int supernodePort;

	private byte[] response = new byte[1024];
	private int port;

	public NodeClient(String[] args) throws IOException {
		supernodeIP = InetAddress.getByName(args[0]);
		supernodePort = Integer.parseInt(args[1]);
		port = Integer.parseInt(args[3]) + 101;
		socket = new DatagramSocket(port);
	}

	public void run() {
		BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));

		while (true) {

			System.out.println("\n<find/peer> <resource-hash>");
			System.out.println("Example: find 698dc19d489c4e4db73e28a713eab07b");
			System.out.println("Example: peer ");
			try {
				String str = obj.readLine();
				String[] vars = str.split("\\s");

				if (vars[0].equalsIgnoreCase("peer")) {
					String hash = vars[1];
					InetAddress peerIP = InetAddress.getByName(vars[2]);
					int peerPort = Integer.parseInt(vars[3]);
					System.out.println("Sending message to peer on port " + peerIP + ":" + peerPort);
					send(hash, peerIP, peerPort);
				} else {
					System.out.println("Sending message to supernode on address " + supernodeIP + ":" + supernodePort);
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < vars.length; i++) {
						sb.append(vars[i]).append(" ");
					}
					send(sb.toString(), supernodeIP, supernodePort);
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	private void send(String data, InetAddress address, int port) {
		try {
			byte[] resource = data.getBytes();
			packet = new DatagramPacket(resource, resource.length, address, port);
			socket.send(packet);

			while (true) {
				try {
					packet = new DatagramPacket(response, response.length);
					socket.setSoTimeout(500);
					socket.receive(packet);

					String resposta = new String(packet.getData(), 0, packet.getLength());
					System.out.println("recebido: " + resposta);
				} catch (IOException e) {
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
