import java.net.InetAddress;

public class Resource {

    private String hash;
    private String fileName;
    private InetAddress ip;
    private int port;

    public Resource(String hash, String fileName, InetAddress ip, int port) {
        this.hash = hash;
        this.fileName = fileName;
        this.ip = ip;
        this.port = port;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    @Override
    public String toString() {
        return "{hash='" + hash + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ip=" + ip +
                ", port=" + port +
                '}';
    }
}
