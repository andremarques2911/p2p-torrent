public class P2P {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Erro: " + args.length + " parametros informados.");
            System.exit(1);
        }
        if (args[3].equalsIgnoreCase("SN")) new SuperNode(args);
        if (args[3].equalsIgnoreCase("N")) new Node(args);
    }
}
