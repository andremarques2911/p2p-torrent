import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SuperNode {

	private ConcurrentHashMap<String, Resource> distributedHashTable;

	public SuperNode(String[] args) {
		distributedHashTable = new ConcurrentHashMap<>();
		try {
			new SuperNodeThread(args, distributedHashTable).start();
			new SuperNodeGroupReceiver(args, distributedHashTable).start();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
