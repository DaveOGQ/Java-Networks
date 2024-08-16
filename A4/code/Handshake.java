public class Handshake {
    private int port;
    private int seq;

    public Handshake(int seq, int port){
        this.port = port;
        this.seq = seq;
    }

    public int getPort() {
        return port;
    }

    public int getSeq() {
        return seq;
    }

}
