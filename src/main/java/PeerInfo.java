public class PeerInfo {
    public String ip;
    public String port;

    @Override
    public String toString() {
        return this.ip + ":" + this.port;
    }

    public PeerInfo(byte[] peerBytes) {
        this.ip = getIp(peerBytes);
        this.port = String.valueOf(getPort(peerBytes));
    }

    public PeerInfo(String ipPortCombination) {
        String ip = ipPortCombination.substring(0, ipPortCombination.lastIndexOf(":"));
        String port = ipPortCombination.substring(ipPortCombination.indexOf(":") + 1, ipPortCombination.length());
        this.ip = ip;
        this.port = port;
    }

    public static String getIp(byte[] peerBytes) {
        if (peerBytes.length != 6) {
            throw new IllegalArgumentException("Peer info must exactly be 6 bytes.");
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            sb.append(peerBytes[i] & 0xFF);
            sb.append(".");
        }

        // remove trailing dot
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static int getPort(byte[] peerBytes) {
        if (peerBytes.length != 6) {
            throw new IllegalArgumentException("Peer info must exactly be 6 bytes.");
        }

        return ((peerBytes[4] & 0xFF) << 8) | (peerBytes[5] & 0xFF);
    }
}
