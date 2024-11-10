import java.util.Arrays;
import java.util.List;

public class Torrent {
    public String announce;
    public long length;
    public byte[] infoHash;
    public List<byte[]> pieceHashes;
    public long pieceLength;

    public Torrent(String announce, long length, byte[] infoHash, List<byte[]> pieceHashes, long pieceLength) {
        this.announce = announce;
        this.length = length;
        this.infoHash = infoHash;
        this.pieceLength = pieceLength;
        this.pieceHashes = pieceHashes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tracker URL: " + announce + "\n");
        sb.append("Length: " + this.length + "\n");
        sb.append("Info Hash: " + this.translateDigest(this.infoHash) + "\n");
        sb.append("Piece Length: " + this.pieceLength + "\n");
        sb.append("Piece Hashes:\n");
        for (byte[] bytes: pieceHashes) {
            sb.append(translateDigest(bytes) + "\n");
        }

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
