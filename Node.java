import java.io.IOException;

public class Node {
	public Node(String[] args) {
		this.run(args);
	}

	public void run(String[] args) {

		try {
			new NodeThread(args).start();
			new Heartbeat(args).start();
			new NodeClient(args).start();
		} catch (IOException e) {

		}
	}
}
