import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
// import com.dampcake.bencode.Bencode; - available if you need it!

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
//    System.out.println("Logs from your program will appear here!");
        String command = args[0];
        if("decode".equals(command)) {
            //  Uncomment this block to pass the first stage
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

    static Object[] decodeBencodeHelper(String bencodedString, int startIndex) {
        if (startIndex >= bencodedString.length()) {
            throw new RuntimeException("Unexpected end of input");
        }

        char firstChar = bencodedString.charAt(startIndex);

        if (Character.isDigit(firstChar)) {
            // String decoding
            int colonIndex = bencodedString.indexOf(':', startIndex);
            if (colonIndex == -1) {
                throw new RuntimeException("Invalid string encoding");
            }
            int length = Integer.parseInt(bencodedString.substring(startIndex, colonIndex));
            String value = bencodedString.substring(colonIndex + 1, colonIndex + 1 + length);
            return new Object[]{value, colonIndex + 1 + length};
        } else if (firstChar == 'i') {
            // Integer decoding
            int endIndex = bencodedString.indexOf('e', startIndex);
            if (endIndex == -1) {
                throw new RuntimeException("Invalid integer encoding");
            }
            long value = Long.parseLong(bencodedString.substring(startIndex + 1, endIndex));
            return new Object[]{value, endIndex + 1};
        } else if (firstChar == 'l') {
            // List decoding
            List<Object> list = new ArrayList<>();
            int index = startIndex + 1;
            while (index < bencodedString.length() && bencodedString.charAt(index) != 'e') {
                Object[] result = decodeBencodeHelper(bencodedString, index);
                list.add(result[0]);
                index = (int) result[1];
            }
            if (index >= bencodedString.length()) {
                throw new RuntimeException("Unterminated list");
            }
            return new Object[]{list, index + 1};
        } else {
            throw new RuntimeException("Unsupported type");
        }
    }

    static Object decodeBencode(String bencodedString) {
        return decodeBencodeHelper(bencodedString, 0)[0];
    }

}
