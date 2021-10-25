import java.net.InetAddress;

public class Peer {
    private InetAddress ip;
    private int port;
    private int timeout;

    public Peer(InetAddress ip, int port, int timeout) {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "{ip=" + ip +
                ", port=" + port +
                ", timeout=" + timeout +
                '}';
    }
}
