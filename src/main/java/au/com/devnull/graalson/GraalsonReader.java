package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.toJsonValue;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.util.Scanner;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonReader implements JsonReader {

    private final Value value;

    InvocationHandler handler;

    public GraalsonReader(Reader reader) {

        this.value = GraalsonProvider.getPolyglotContext().eval("js", new Scanner(reader).useDelimiter("\\Z").next());
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
