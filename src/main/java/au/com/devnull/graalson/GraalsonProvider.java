package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonValue.toJsonValue;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;

/**
 *
 * @author wozza
 */
public class GraalsonProvider extends JsonProvider implements JsonReaderFactory, JsonWriterFactory, JsonBuilderFactory, JsonGeneratorFactory, JsonParserFactory {

    private static Context polyglotContext = null;

    private static JsonProvider instance = null;

    private static Map<String, ? super Object> config = new HashMap<>();

    public static JsonProvider provider() {

        if (instance == null) {
            instance = new GraalsonProvider();
        }
        return instance;
    }

    static String[] buildConfig() {
        List<String> args = new ArrayList<>();
        args.add(config.getOrDefault("replacer", "null").toString());
        Object spaces = config.getOrDefault("spaces", "");
        if (spaces instanceof Number) {
            args.add(spaces.toString());
        } else {
            args.add("'" + spaces.toString() + "'");
        }
        return args.toArray(new String[args.size()]);
    }

    @Override
    public JsonParser createParser(Reader reader) {
        return new GraalsonParser(reader);
    }

    @Override
    public JsonParser createParser(InputStream in) {
        return new GraalsonParser(new InputStreamReader(in));
    }

    @Override
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        return this;
    }

    @Override
    public JsonParser createParser(InputStream in, Charset charset) {
        return new GraalsonParser(new InputStreamReader(in, charset));
    }

    @Override
    public JsonParser createParser(JsonObject obj) {
        return new GraalsonParser(obj);
    }

    @Override
    public JsonParser createParser(JsonArray array) {
        return new GraalsonParser(array);
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
    public JsonGenerator createGenerator(OutputStream out, Charset charset) {
        return this.createGenerator(new OutputStreamWriter(out, charset));
    }

    @Override
    public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        return this;
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
    public JsonReader createReader(InputStream in, Charset charset) {
        return this.createReader(in);

    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return config;
    }

    @Override
    public JsonWriter createWriter(OutputStream out, Charset charset) {
        return this.createWriter(out);
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return new GraalsonGenerator(writer).gwriter;
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        return this.createWriter(new OutputStreamWriter(out));
    }

    @Override
    public JsonWriterFactory createWriterFactory(Map<String, ? extends Object> newConfig) {
        if (newConfig != null) {
            config.putAll(newConfig);
        }
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

    /**
     * *
     * Equivalent to
     * System.setProperty("javax.xml.transform.TransformerFactory",
     * "au.com.devnull.graalson.trax.GraalsonTransformerFactory");
     */
    public static void useJavaxXmlTransformTransformerFactory() {
        System.setProperty("javax.xml.transform.TransformerFactory", "au.com.devnull.graalson.trax.GraalsonTransformerFactory");
    }

    public static void setPloyglotContext(Context context) {
        polyglotContext = context;

    }

    public static Context defaultPolyglotContext() {
        return Context.newBuilder("js")
                .allowPolyglotAccess(PolyglotAccess.ALL)
                .allowExperimentalOptions(true)
                .allowAllAccess(true)
                //.option("js.experimental-foreign-object-prototype", "true")
                .allowHostAccess(HostAccess.ALL).build();
    }

    public static Context getPolyglotContext() {
        if (polyglotContext != null) {
            return polyglotContext;
        }

        return polyglotContext = defaultPolyglotContext();
    }

    public JsonValue createValue(Object obj) {
        return toJsonValue(obj);
    }

    public GraalsonArrayBuilder createArrayBuilder(Collection<?> collection) {
        return new GraalsonArrayBuilder(collection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GraalsonObjectBuilder createObjectBuilder(Map map) {
        return new GraalsonObjectBuilder(map);
    }

    public GraalsonObjectBuilder createObjectBuilder(JsonObject obj) {
        return new GraalsonObjectBuilder(obj);
    }

    public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
        if (!(source instanceof JsonObject) || !(target instanceof JsonObject)) {
            throw new IllegalArgumentException("Only JsonObject supported for merge-diff");
        }
        JsonStructure mergePatch = GraalsonMergePatch.createDiff((JsonObject) source, (JsonObject) target);
        return new GraalsonMergePatch(mergePatch);
    }

    public JsonPatchBuilder createPatchBuilder(JsonValue source, JsonValue target) {
        return new GraalsonPatchBuilder();
    }

    @Override
    public JsonNumber createValue(BigInteger value) {
        return new GraalsonNumber(value);
    }

    @Override
    public JsonNumber createValue(BigDecimal value) {
        return new GraalsonNumber(value);
    }

    @Override
    public JsonNumber createValue(double value) {
        return new GraalsonNumber(value);
    }

    @Override
    public JsonNumber createValue(long value) {
        return new GraalsonNumber(value);
    }

    @Override
    public JsonNumber createValue(int value) {
        return new GraalsonNumber(value);
    }

    @Override
    public JsonString createValue(String value) {
        return new GraalsonString(value);
    }

    @Override
    public JsonMergePatch createMergePatch(JsonValue patch) {
        return new GraalsonMergePatch(patch);
    }

    @Override
    public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
        return GraalsonPatch.createDiff(source, target);
    }

    @Override
    public JsonPatch createPatch(JsonArray array) {
        return new GraalsonPatch(array);
    }

    @Override
    public JsonPatchBuilder createPatchBuilder(JsonArray array) {
        return new GraalsonPatchBuilder(array);
    }

    @Override
    public JsonPatchBuilder createPatchBuilder() {
        return new GraalsonPatchBuilder();
    }

    @Override
    public JsonPointer createPointer(String jsonPointer) {
        return new GraalsonPointer(jsonPointer);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(JsonArray array) {
        return new GraalsonArrayBuilder(array);
    }

}
