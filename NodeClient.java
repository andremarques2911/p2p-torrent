import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeClient extends Thread {
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	protected InetAddress supernodeIP = null;
	protected int supernodePort;
	private byte[] response = new byte[1024];

	public NodeClient(String[] args, DatagramSocket socket) throws IOException {
		supernodeIP = InetAddress.getByName(args[0]);
		supernodePort = Integer.parseInt(args[1]);
		this.socket = socket;
	}

	private InetAddress fixIp(InetAddress address) throws UnknownHostException {
		return InetAddress.getByName(address.getHostAddress().replace("/127.0.0.1", ""));
	}

	public void run() {
		BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("\n<find/peer> <resource-hash>");
			System.out.println("Example: find 0zx431221");
			System.out.println("Example: peer <file-name> <peer-ip> <peer-port>");
			try {
				String str = obj.readLine();
				String[] vars = str.split("\\s");

				if (vars[0].equalsIgnoreCase("peer")) {
					String hash = vars[1];
					InetAddress peerIP = InetAddress.getByName(vars[2]);
					System.out.println("PEER IP: " + peerIP);
					int peerPort = Integer.parseInt(vars[3]);
					System.out.println("Sending message to peer on port " + peerIP + ":" + peerPort);
					send("find " + hash, peerIP, peerPort);
				} else {
					supernodeIP = fixIp(supernodeIP);
					System.out.println("Sending message to supernode on address " + supernodeIP + ":" + supernodePort);
					StringBuilder sb = new StringBuilder("find ");
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
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
