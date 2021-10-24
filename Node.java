import java.io.IOException;
import java.net.DatagramSocket;

public class Node {
	private DatagramSocket socket;

	public Node(String[] args) {
		try {
			int port = Integer.parseInt(args[3]);
			socket = new DatagramSocket(port);

			new NodeThread(args, socket).start();
			new Heartbeat(args, socket).start();
			new NodeClient(args, socket).start();
		} catch (IOException e) {

		}
	}
}
