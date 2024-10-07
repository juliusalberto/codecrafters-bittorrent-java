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

    static Object decodeBencode(String bencodedString) {
        if (Character.isDigit(bencodedString.charAt(0))) {
            int firstColonIndex = 0;
            for(int i = 0; i < bencodedString.length(); i++) {
                if(bencodedString.charAt(i) == ':') {
                    firstColonIndex = i;
                    break;
                }
            }
            int length = Integer.parseInt(bencodedString.substring(0, firstColonIndex));
            return bencodedString.substring(firstColonIndex+1, firstColonIndex+1+length);
        } else if (bencodedString.charAt(0) == 'i') {
            int eIndex = 0;

            for (int i = 1; i < bencodedString.length(); i++) {
                if (bencodedString.charAt(i) == 'e') {
                    eIndex = i;
                    break;
                }
            }

            // Now we already have the index of e
            // We want to get the substring from 1 - e
            String numString = bencodedString.substring(1, eIndex);
            return Long.parseLong(numString);
        } else if (bencodedString.charAt(0) == 'l') {
            // We know that this is a list
            // First we create a list of object first

            List<Object> list = new ArrayList<>();

            // Start at 1 because we want to skip the l
            int index = 1;

            while (index < bencodedString.length() && bencodedString.charAt(index) != 'e') {
                Object item = decodeBencode(bencodedString.substring(index));

                if (item instanceof String) {
                    // We know that the form of string is [num]:[string]
                    // so we can skip the index by string.length() + 1 (for the : sign) + num.toString().length()
                    int stringLength = ((String) item).length();
                    int lengthLength = String.valueOf(stringLength).length();
                    index += stringLength + lengthLength + 1;
                } else if (item instanceof Long) {
                    // We know that the long has i and e between.
                    int numLength = item.toString().length();
                    index += numLength + 2;
                }
            }

            if (index >= bencodedString.length()) {
                // should stop at e or len - 1
                throw new RuntimeException("Unterminated list");
            }

            return list;
        }
        else {
            throw new RuntimeException("Only strings are supported at the moment");
        }
    }

}
