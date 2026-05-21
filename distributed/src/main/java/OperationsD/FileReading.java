package OperationsD;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.List;


/* Used help from Chatgpt to understand how to implement this class to read the json file
especially sith the loadRecords function since I could not get it to work. Tried jackson but
 Chatgpt suggested Gson. ALso, it recommended to implement it to use the resources directory.
 */
public class FileReading {

    // Gson object used to convert JSON data into Java objects.
    private static final Gson GSON = new Gson();

    // Stores the expected JSON structure, which is a list of Records.
    private static final Type LIST_TYPE = new TypeToken<List<Records>>() {}.getType();

    public static List<Records> loadRecords(String resourcePath) {

        // Ensures the path starts with "/" so it can be read correctly from resources.
        String path = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;

        InputStream is =FileReading.class.getResourceAsStream(path);
        if (is == null) {
            throw new RuntimeException("Cannot find resource: " + path +
                    " (make sure it's in src/main/resources)");
        }

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, LIST_TYPE);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON from " + path, e);
        }
    }
}
