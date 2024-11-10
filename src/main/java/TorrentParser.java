import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TorrentParser {
    public static String parseTorrent(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b: bytes) {
            if (b >= 32 && b < 127) {
                result.append((char) b);
            } else {
                // For non-printable characters, use a placeholder or skip
                result.append("\\x").append(String.format("%02x", b));
            }
        }

        JsonObject decoded = (JsonObject) BencodeDecoder.decodeBencode(result.toString());
        Gson gson = new Gson();
        String announce = decoded.get("announce").getAsString();
        JsonObject info = decoded.getAsJsonObject("info");

        StringBuilder sb = new StringBuilder();
        sb.append("Tracker URL: " + announce);
        sb.append("Length: " + info.get("length").getAsString());

        return sb.toString();
    }


}
