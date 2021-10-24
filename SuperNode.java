import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SuperNode {
	public SuperNode(String[] args) {
		ConcurrentHashMap<String, Resource> distributedHashTable = new ConcurrentHashMap<>();
		try {
			new SuperNodeThread(args, distributedHashTable).start();
			new SuperNodeGroupReceiver(args, distributedHashTable).start();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
