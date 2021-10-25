public class P2P {
    public static void main(String[] args) {
        if (args.length < 3 || args.length > 4) {
            System.err.println("Error: " + args.length + " parameter informed.");
            String sb = "Run a supernode: " +
                    "java P2P <GroupIP> <GroupPort> <SuperNodePort> \n" +
                    "Run a node: " +
                    "java P2P <SuperNodeIP> <SuperNodePort> <Resources> <NodePort>\n";
            System.err.println(sb);
            System.exit(1);
        }
        if (args.length == 3) {
            new SuperNode(args);
        } else {
            new Node(args);
        }
    }
}
