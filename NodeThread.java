import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeThread extends Thread {
	private final DatagramSocket socket;
	private final InetAddress supernodeIP;
	private final int supernodePort;
	private final byte[] response = new byte[1024];
	private final String hashes;

	public NodeThread(String[] args, DatagramSocket socket) throws IOException {
		supernodeIP = InetAddress.getByName(args[0]);
		supernodePort = Integer.parseInt(args[1]);
		this.socket = socket;
		hashes = args[2];
	}

	public void run() {
		try {
			byte[] resource = ("create " + hashes).getBytes();
			DatagramPacket packet = new DatagramPacket(resource, resource.length, supernodeIP, supernodePort);
			socket.send(packet);
		} catch (IOException e) {
			socket.close();
		}

		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(response, response.length);
				socket.receive(packet);

				String data = new String(packet.getData(), 0, packet.getLength());

				String[] vars = data.split("\\s");
				String received = data.length() > 100 ? data.substring(0, 100) : data;
				System.out.println("Resource at: " + received);
				if (vars[0].equalsIgnoreCase("find")) {
					System.out.println("Sending file " + vars[1] + "to " + packet.getAddress() + ":" + packet.getPort());
					try {
						byte[] file = Files.readAllBytes(Path.of("./files/" + vars[1]));
						DatagramPacket filePacket = new DatagramPacket(file, file.length, packet.getAddress(), packet.getPort());
						socket.send(filePacket);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

}
