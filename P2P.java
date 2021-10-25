public class P2P {
    public static void main(String[] args) {
        //SuperNode: java P2P <GroupIP> <GroupPort> <Port>
        //ClientNode: java P2P localhost <SuperNodePort> <Resources> <Port>
        if (args.length < 3 || args.length > 4) {
            System.err.println("Erro: " + args.length + " parametros informados.");
            System.exit(1);
        }
        if (args.length == 3) {
            new SuperNode(args);
        } else {
            new Node(args);
        }
    }
}
