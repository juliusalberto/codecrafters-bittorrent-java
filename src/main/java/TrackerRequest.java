public record TrackerRequest( byte[] infoHash, // 20-byte raw info hash
                              String peerId,   // unique 20-character peer ID
                              int port,        // listening port
                              long uploaded,
                              long downloaded,
                              long left,
                              boolean compact) {

    public String encodedInfoHash() {
        StringBuilder sb = new StringBuilder();
        for (byte b : infoHash) {
            sb.append("%").append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }
}
