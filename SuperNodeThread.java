import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class SuperNodeThread extends Thread {
    protected DatagramSocket socket;
    protected InetAddress groupIP;
    protected int groupPort;
    protected int port;
    protected byte[] resource = new byte[1024];
    protected byte[] response = new byte[1024];
    protected List<String> resourceList = new ArrayList<>();
    protected List<InetAddress> resourceAddr = new ArrayList<>();
    protected List<Integer> resourcePort = new ArrayList<>();
    protected List<Integer> timeoutVal = new ArrayList<>();

    public SuperNodeThread(String[] args) throws IOException {
        groupIP = InetAddress.getByName(args[0]);
        groupPort = Integer.parseInt(args[1]);
        port = Integer.parseInt(args[2]);
        socket = new DatagramSocket(port);
    }

    public void run() {

        while (true) {
			try {
				// recebe datagrama
				DatagramPacket packet = new DatagramPacket(resource, resource.length);
				socket.setSoTimeout(500);
				socket.receive(packet);
				System.out.print("Recebi!");
								
				// processa o que foi recebido, adicionando a uma lista
				String content = new String(packet.getData(), 0, packet.getLength());
				InetAddress peerIP = packet.getAddress();
				int peerPort = packet.getPort();
				String vars[] = content.split("\\s");

                if (vars[0].equals("find") && vars.length > 1) {
                    this.send(vars[1]);
                }
				
				if (vars[0].equals("create") && vars.length > 1) {
					int j;
					
					for (j = 0; j < resourceList.size(); j++) {
						if (resourceList.get(j).equals(vars[1]))
							break;
					}
					
					if (j == resourceList.size()) {
						resourceList.add(vars[1]);
						resourceAddr.add(peerIP);
						resourcePort.add(peerPort);
						timeoutVal.add(15);		/* 500ms * 15 = 7.5s (enough for 5s heartbeat) */
						
						response = "OK".getBytes();
					} else {
						response = "NOT OK".getBytes();
					}
					
					packet = new DatagramPacket(response, response.length, peerIP, peerPort);
					socket.send(packet);
				}
				
				if (vars[0].equals("list") && vars.length > 1) {
					for (int j = 0; j < resourceList.size(); j++) {
						if (resourceList.get(j).equals(vars[1])) {
							for (int i = 0; i < resourceList.size(); i++) {
								String data = new String(resourceList.get(i) + " " + resourceAddr.get(i).toString() + " " + resourcePort.get(i).toString());
								response = data.getBytes();
								
								packet = new DatagramPacket(response, response.length, peerIP, peerPort);
								socket.send(packet);
							}
							break;
						}
					}
				}
				
				if (vars[0].equals("heartbeat") && vars.length > 1) {
					System.out.print("\nheartbeat: " + vars[1]);
					for (int i = 0; i < resourceList.size(); i++) {
						if (resourceList.get(i).equals(vars[1]))
							timeoutVal.set(i, 15);
					}
				}
			} catch (IOException e) {
				// decrementa os contadores de timeout a cada 500ms (em função do receive com timeout)
				for (int i = 0; i < timeoutVal.size(); i++) {
					timeoutVal.set(i, timeoutVal.get(i) - 1);
					if (timeoutVal.get(i) == 0) {
						System.out.println("\nuser " + resourceList.get(i) + " is dead.");
						resourceList.remove(i);
						resourceAddr.remove(i);
						resourcePort.remove(i);
						timeoutVal.remove(i);
					}
				}
				System.out.print(".");
			}
		}
    }

    private void send(String message) {
        try {
            byte[] out = new byte[256];
            out = message.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(out, out.length, this.groupIP, this.groupPort);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }    
}
