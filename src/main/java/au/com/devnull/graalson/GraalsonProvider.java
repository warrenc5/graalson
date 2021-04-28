package au.com.devnull.graalson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonProvider extends JsonProvider implements JsonReaderFactory, JsonWriterFactory, JsonBuilderFactory {

    public static Context polyglotContext = null;

    private static JsonProvider instance = null;

    public static JsonProvider provider() {

        if (instance == null) {
            instance = new GraalsonProvider();
        }
        return instance;
    }

    @Override
    public JsonParser createParser(Reader reader) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JsonParser createParser(InputStream in) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        return new GraalsonGenerator(writer);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        return this.createGenerator(new OutputStreamWriter(out));
    }

    @Override
    public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JsonReader createReader(Reader reader) {
        return new GraalsonReader(reader);
    }

    @Override
    public JsonReader createReader(InputStream in) {
        return this.createReader(new InputStreamReader(in));
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return new GraalsonGenerator(writer);
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        return this.createWriter(new OutputStreamWriter(out));
    }

    @Override
    public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        return this;
    }

    @Override
    public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        return this;
    }

    @Override
    public JsonObjectBuilder createObjectBuilder() {
        return new GraalsonObjectBuilder();
    }

    @Override
    public JsonArrayBuilder createArrayBuilder() {
        return new GraalsonArrayBuilder();
    }

    @Override
    public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
        return this;
    }

    public static JsonValue toJsonValue(Value o) {
        if (o.hasArrayElements()) {
            return new GraalsonArray(o);
        } else if (o.hasMembers()) {
            return new GraalsonObject(o);
        } else if (o.isNumber()) {
            return new GraalsonNumber(o);
        } else if (o.isString()) {
            return new GraalsonString(o);
        } else if (o.isBoolean()) {
            return new GraalsonBoolean(o);
        }

        throw new IllegalArgumentException(o == null ? "null" : (o.getClass() + " " + o.toString()));
    }

    public static <T extends JsonValue> T toJsonValue(Value o, Class<T> jClass) {

        if (jClass.equals(JsonObject.class)) {
            return (T) new GraalsonObject(o);
        } else if (jClass.equals(JsonArray.class)) {
            return (T) new GraalsonArray(o);
        } else if (jClass.equals(JsonNumber.class)) {
            return (T) new GraalsonNumber(o);
        } else if (jClass.equals(JsonString.class)) {
            return (T) new GraalsonString(o);
        }
        throw new IllegalArgumentException(o == null ? "null" : (o.getClass() + " " + o.toString()));
    }

    public static List toJava(JsonArray value) {
        List result = new ArrayList();
        for (int i = 0; i < value.size(); i++) {
            Object v = toJava(value.get(i));
            result.add(v);
        }
        return result;
    }

    public static Map toJava(JsonObject value) {
        Map result = new HashMap();
        for (Entry<String, JsonValue> e : value.entrySet()) {
            Object v = toJava(e.getValue());
            result.put(e.getKey(), v);
        }
        return result;
    }

    public static Object toJava(JsonValue value) {

        switch (value.getValueType()) {
            case NUMBER:
                return ((JsonNumber) value).intValue();
            case STRING:
                return ((JsonString) value).getString();
            case OBJECT:
                return toJava((JsonObject) value);
            case ARRAY:
                return toJava((JsonArray) value);
            case FALSE:
                return ((GraalsonBoolean) value).getBoolean();
            case NULL:
                return null;
        }
        throw new IllegalArgumentException(value.toString());
    }

    @Override
    public JsonReader createReader(InputStream in, Charset charset) {
        return this.createReader(in);

    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public JsonWriter createWriter(OutputStream out, Charset charset) {
        return this.createWriter(out);
    }

    public static Context getPolyglotContext() {
        if (polyglotContext != null) {
            return polyglotContext;
        }

        return polyglotContext = Context.newBuilder("js")
                .allowPolyglotAccess(PolyglotAccess.ALL)
                .allowExperimentalOptions(true)
                .allowAllAccess(true)
                .option("js.experimental-foreign-object-prototype", "true")
                .allowHostAccess(HostAccess.ALL).build();
    }

}
