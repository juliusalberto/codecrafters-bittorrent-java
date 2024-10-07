import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
// import com.dampcake.bencode.Bencode; - available if you need it!

class DecodeResult {
    Object value;
    int nextIndex;

    DecodeResult(Object value, int nextIndex) {
        this.value = value;
        this.nextIndex = nextIndex;
    }
}

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        String command = args[0];
        if ("decode".equals(command)) {
            String bencodedValue = args[1];
            Object decoded;
            try {
                decoded = decodeBencode(bencodedValue);
            } catch(RuntimeException e) {
                System.out.println(e.getMessage());
                return;
            }
            System.out.println(gson.toJson(decoded));
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    static DecodeResult decodeBencodeHelper(String bencodedString, int startIndex) {
        if (startIndex >= bencodedString.length()) {
            throw new RuntimeException("Unexpected end of input");
        }

        char firstChar = bencodedString.charAt(startIndex);

        if (Character.isDigit(firstChar)) {
            return decodeString(bencodedString, startIndex);
        } else if (firstChar == 'i') {
            return decodeInteger(bencodedString, startIndex);
        } else if (firstChar == 'l') {
            return decodeList(bencodedString, startIndex);
        } else if (firstChar == 'd') {
            return decodeDictionary(bencodedString, startIndex);
        } else {
            throw new RuntimeException("Unsupported type");
        }
    }

    static DecodeResult decodeString(String bencodedString, int startIndex) {
        int colonIndex = bencodedString.indexOf(':', startIndex);
        if (colonIndex == -1) {
            throw new RuntimeException("Invalid string encoding");
        }
        int length = Integer.parseInt(bencodedString.substring(startIndex, colonIndex));
        String value = bencodedString.substring(colonIndex + 1, colonIndex + 1 + length);
        return new DecodeResult(value, colonIndex + 1 + length);
    }

    static DecodeResult decodeInteger(String bencodedString, int startIndex) {
        int endIndex = bencodedString.indexOf('e', startIndex);
        if (endIndex == -1) {
            throw new RuntimeException("Invalid integer encoding");
        }
        long value = Long.parseLong(bencodedString.substring(startIndex + 1, endIndex));
        return new DecodeResult(value, endIndex + 1);
    }

    static DecodeResult decodeList(String bencodedString, int startIndex) {
        List<Object> list = new ArrayList<>();
        int index = startIndex + 1;
        while (index < bencodedString.length() && bencodedString.charAt(index) != 'e') {
            DecodeResult result = decodeBencodeHelper(bencodedString, index);
            list.add(result.value);
            index = result.nextIndex;
        }
        if (index >= bencodedString.length()) {
            throw new RuntimeException("Unterminated list");
        }
        return new DecodeResult(list, index + 1);
    }

    static DecodeResult decodeDictionary(String bencodedString, int startIndex) {
        Map<Object, Object> hashMap = new HashMap<>();
        int index = startIndex + 1;
        while (index < bencodedString.length() && bencodedString.charAt(index) != 'e') {
            DecodeResult key = decodeBencodeHelper(bencodedString, index);
            index = key.nextIndex;
            DecodeResult value = decodeBencodeHelper(bencodedString, index);
            index = value.nextIndex;
            hashMap.put(key.value, value.value);
        }
        return new DecodeResult(hashMap, index + 1);
    }

    static Object decodeBencode(String bencodedString) {
        return decodeBencodeHelper(bencodedString, 0).value;
    }
}