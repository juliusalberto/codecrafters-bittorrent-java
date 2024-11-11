import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PeerClient {

    private static int HANDSHAKE_BUFFER_SIZE = 68; //total buffer size for handshake

    public static byte[] createHandshake(Torrent torrent, String peerId) {
        // first we want to translate the peer ID into a byte
        byte[] peerIdBytes = peerId.getBytes(StandardCharsets.UTF_8);
        byte[] infoHash = torrent.infoHash;

        if (infoHash.length != 20 || peerIdBytes.length != 20) {
            throw new IllegalArgumentException();
        }

        ByteBuffer buffer = ByteBuffer.allocate(HANDSHAKE_BUFFER_SIZE);
        buffer.put((byte) 19); // length of protocol
        buffer.put("BitTorrent protocol".getBytes());

        // reserved bytes
        buffer.put(new byte[8]);

        // info hash 20 bytes
        buffer.put(infoHash);

        // peer id
        buffer.put(peerIdBytes);
        return buffer.array();
    }

    public static byte[] doHandshake(byte[] handshake, PeerInfo peer) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(peer.ip, Integer.parseInt(peer.port)), 5000);

            OutputStream out = socket.getOutputStream();
            out.write(handshake);
            out.flush();

            // receive back
            InputStream in = socket.getInputStream();
            byte[] response = new byte[68];
            int bytesRead = in.read(response);

            if (bytesRead == 68) {
                return response;
            } else {
                throw new IOException("Handshake failed!");
            }


        } catch (IOException ioException) {
            System.out.println("There's a problem with the socket: " + ioException.getMessage());
        }

        return new byte[68];
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
