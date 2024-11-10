import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.dampcake.bencode.Bencode;



public class Main {
    public static void main(String[] args) throws Exception {
        String command = args[0];
        if ("decode".equals(command)) {
            String bencodedValue = args[1];
            Object decoded;
            try {
                decoded = BencodeDecoder.decodeBencode(bencodedValue);
            } catch(RuntimeException e) {
                System.out.println(e.getMessage());
                return;
            }
            System.out.println(new Gson().toJson(decoded));
        } else if ("info".equals(command)) {
            String fileName = args[1];
            byte[] fileBytes = Files.readAllBytes(Path.of(fileName));

        }
        else {
            System.out.println("Unknown command: " + command);
        }
    }
}