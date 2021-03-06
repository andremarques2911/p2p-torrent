import java.io.IOException;
import java.net.DatagramSocket;

public class Node {

	public Node(String[] args) {
		try {
			int port = Integer.parseInt(args[3]);
			DatagramSocket socket = new DatagramSocket(port);

			new NodeThread(args, socket).start();
			new Heartbeat(args, socket).start();
			new NodeClient(args, socket).start();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
