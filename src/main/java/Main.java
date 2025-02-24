import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.dampcake.bencode.Bencode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Main {
    public static String PEER_ID = "julius-clientyJQTofi";

    public static void main(String[] args) throws Exception {
        String command = args[0];
        if ("decode".equals(command)) {
            String bencodedValue = args[1];
            JsonElement decoded;
            try {
                decoded = BencodeDecoder.decodeBencode(bencodedValue);
            } catch(RuntimeException e) {
                System.out.println(e.getMessage());
                return;
            }
            System.out.println(new Gson().toJson(decoded));
        } else if ("info".equals(command)) {
            String fileName = args[1];
            Torrent torrent = parseTorrent(fileName);

            System.out.println(torrent.toString());
        } else if ("peers".equals(command)) {
            String fileName = args[1];

            Torrent torrent = parseTorrent(fileName);
            TrackerRequest request = new TrackerRequest(torrent.infoHash, PEER_ID, 6881, 0, 0, torrent.length, true);

            Map<String, Object> response = TrackerClient.sendRequest(request, torrent.announce);
            List<PeerInfo> peers = TrackerClient.extractPeers(response);
            for (PeerInfo peer: peers) {
                System.out.println(peer);
            }
        } else if ("handshake".equals(command)) {
            String fileName = args[1];
            String ip_port = args[2];
            Torrent torrent = parseTorrent(fileName);
            PeerInfo peerInfo = new PeerInfo(ip_port);

            byte[] handshake = PeerClient.createHandshake(torrent, PEER_ID);
            byte[] result = PeerClient.doHandshake(handshake, peerInfo);
            byte[] peerIdBytes = Arrays.copyOfRange(result, result.length - 20, result.length);

            // print from bytes to hex
            System.out.println("Peer ID: " + PeerClient.bytesToHex(peerIdBytes));

        }
        else {
            System.out.println("Unknown command: " + command);
        }
    }

    public static Torrent parseTorrent(String fileName) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Path.of(fileName));
        Torrent torrent = TorrentParser.parseTorrent(fileBytes);

        return torrent;
    }
}