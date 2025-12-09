package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonValue.jsonParse;
import static au.com.devnull.graalson.GraalsonValue.toJsonValue;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonReader implements JsonReader {

    private Value value;
    private final Reader reader;

    public GraalsonReader(final Reader reader) {
        this.reader = reader;
    }

    private JsonStructure toJsonStructure(Value value) {
        if (value.hasMembers()) {
            return (JsonStructure) toJsonValue(value);
        } else if (value.hasArrayElements()) {
            return (JsonStructure) toJsonValue(value);
        } else {
            throw new IllegalArgumentException("Value is not a JSON object or array. " + value);
        }
    }

    @Override
    public JsonStructure read() {
        try (this.reader) {
            this.value = jsonParse(new Scanner(reader).useDelimiter("\\Z").next());
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
        return toJsonStructure(value);
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
        try {
            reader.close();
        } catch (IOException ex) {
            //never?
        }
    }
}
