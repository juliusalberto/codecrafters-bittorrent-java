import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TrackerClient {
    public static Map<String, Object> sendRequest(TrackerRequest request, String baseUrl) throws IOException, InterruptedException {
        String url = buildUrl(request, baseUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

        Bencode bencode = new Bencode(true);
        return bencode.decode(response.body(), Type.DICTIONARY);
    }

    public static List<PeerInfo> extractPeers(Map<String, Object> response) {
        ByteBuffer peerBytes = (ByteBuffer) response.get("peers");
        List<PeerInfo> result = new ArrayList<>();

        while (peerBytes.remaining() >= 6) {
            byte[] currentPeerBytes = new byte[6];
            peerBytes.get(currentPeerBytes);
            result.add(new PeerInfo(currentPeerBytes));
        }

        return result;
    }

    private static String buildUrl(TrackerRequest request, String baseUrl) {
        return baseUrl + "?info_hash=" + request.encodedInfoHash()
                + "&peer_id=" + URLEncoder.encode(request.peerId(), StandardCharsets.UTF_8)
                + "&port=" + request.port()
                + "&uploaded=" + request.uploaded()
                + "&downloaded=" + request.downloaded()
                + "&left=" + request.left()
                + "&compact=" + (request.compact() ? "1" : "0");
    }
}
