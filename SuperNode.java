import java.io.IOException;

public class SuperNode {
	public SuperNode(String[] args) {
		this.run(args);
	}

	public void run(String[] args) {
		try {
			new SuperNodeThread(args).start();
			new SuperNodeGroupReceiver(args).start();
		} catch (IOException e) {

		}
	}
}
