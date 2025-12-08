package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonValue.jsonParse;
import static au.com.devnull.graalson.GraalsonValue.toJsonValue;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import java.io.Reader;
import java.util.Scanner;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonReader implements JsonReader {

    private final Value value;

    public GraalsonReader(Reader reader) {
        this.value = jsonParse(new Scanner(reader).useDelimiter("\\Z").next());
    }

    @Override
    public JsonStructure read() {
        return (JsonStructure) toJsonValue(value);
    }

    @Override
    public JsonObject readObject() {
        return (JsonObject) this.read();
    }

    @Override
    public JsonArray readArray() {
        return (JsonArray) this.read();
    }

    @Override
    public void close() {
    }

}
