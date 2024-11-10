import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TorrentParser {
    public static Torrent parseTorrent(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b: bytes) {
            if (b >= 32 && b < 127) {
                result.append((char) b);
            } else {
                // For non-printable characters, use a placeholder or skip
                result.append((char)(b & 0xff));
            }
        }
        JsonElement decodedElem = BencodeDecoder.decodeBencode(result.toString());
        JsonObject decoded = decodedElem.getAsJsonObject();
        String announce = decoded.get("announce").getAsString();
        JsonObject info = decoded.getAsJsonObject("info");

        return new Torrent(announce, info.get("length").getAsLong(),BencodeDecoder.findInfoHash(bytes));
    }


}
