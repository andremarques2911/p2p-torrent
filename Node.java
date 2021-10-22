import java.io.IOException;

public class Node {
	public Node(String[] args) {
		this.run(args);
	}

	public void run(String[] args) {
		// if (args.length != 3) {
		// 	System.out.println("Uso: java Node <supernode_ip> <supernode_port> \"<message>\" <localport>");
		// 	System.out.println("<message> is:");
		// 	System.out.println("create nickname");
		// 	System.out.println("list nickname");
		// 	System.out.println("wait");
		// 	return;
		// } else {
		// }
		try {
			new NodeThread(args).start();
			new Heartbeat(args).start();
			new NodeClient(args).start();
		} catch (IOException e) {

		}
	}
}
