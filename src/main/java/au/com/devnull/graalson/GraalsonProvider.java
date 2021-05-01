package au.com.devnull.graalson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
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

    private static String[] buildConfig() {
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
    public JsonWriter createWriter(Writer writer) {
        return new GraalsonGenerator(writer);
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

    static Value toValue(JsonValue value) {
        return ((GraalsonObject) value).getGraalsonValue();
    }

    static Value valueFor(Class<? extends Object> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            //Map<Object, Object> map = getPolyglotContext().eval("js", "{}").as(Map.class);
            //assert ((Map<Object, Object>) getPolyglotContext().eval("js", "[{}]").as(Object.class)).get(0) instanceof Map;
            //FIXME assert fails, map is null
            //return Value.asValue(ProxyObject.fromMap(new HashMap())); //
            return getPolyglotContext().getBindings("js").getMember("Object").execute();
        } else if (List.class.isAssignableFrom(clazz)) {
            List<Object> list = getPolyglotContext().eval("js", "[]").as(List.class);
            return Value.asValue(list);
        }
        throw new IllegalArgumentException(clazz.getCanonicalName());
    }

    static JsonValue toJsonValue(Value o) {
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

    static <T extends JsonValue> T toJsonValue(Value o, Class<T> jClass) {

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

    static List toJava(JsonArray value) {
        List result = new ArrayList();
        for (int i = 0; i < value.size(); i++) {
            Object v = toJava(value.get(i));
            result.add(v);
        }
        return result;
    }

    static Map toJava(JsonObject value) {
        Map result = new HashMap();
        for (Entry<String, JsonValue> e : value.entrySet()) {
            Object v = toJava(e.getValue());
            result.put(e.getKey(), v);
        }
        return result;
    }

    static Object toJava(JsonValue value) {

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
        //TODO allow for pretty print config
        return this.config;
    }

    @Override
    public JsonWriter createWriter(OutputStream out, Charset charset) {
        return this.createWriter(out);
    }

    static Context getPolyglotContext() {
        if (polyglotContext != null) {
            return polyglotContext;
        }

        //TODO graalvm version specific context initialization
        return polyglotContext = Context.newBuilder("js")
                .allowPolyglotAccess(PolyglotAccess.ALL)
                .allowExperimentalOptions(true)
                .allowAllAccess(true)
                //.option("js.experimental-foreign-object-prototype", "true")
                .allowHostAccess(HostAccess.ALL).build();
    }

    static Value jsonParse(String value) {
        return GraalsonProvider.getPolyglotContext().eval("js", "value= " + value);
    }

    static String stringify(Value context) {

        getPolyglotContext().getBindings("js").putMember("mine", context);
        String script = MessageFormat.format("result = JSON.stringify(mine,{0},{1})", (Object[]) buildConfig());
        getPolyglotContext().eval("js", script);
        Value result = getPolyglotContext().getBindings("js").getMember("result");
        return result.toString();
    }

}
