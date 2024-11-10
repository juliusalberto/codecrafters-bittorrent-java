import java.util.Arrays;

public class Torrent {
    public String announce;
    public long length;
    public byte[] infoHash;

    public Torrent(String announce, long length, byte[] infoHash) {
        this.announce = announce;
        this.length = length;
        this.infoHash = infoHash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tracker URL: " + announce + "\n");
        sb.append("Length: " + this.length + "\n");
        sb.append("Info Hash: " + this.translateDigest(this.infoHash));

        return sb.toString();
    }

    private String translateDigest(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }
}
