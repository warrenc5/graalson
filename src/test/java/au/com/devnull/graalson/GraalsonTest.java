package au.com.devnull.graalson;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author wozza
 */
public class GraalsonTest {

    @Test
    public void testBuilder() throws JSONException {

        Map<String, Object> config = new HashMap<>();
        //FIXME
        //config.put("replacer", null);
        config.put("spaces", Integer.valueOf(4));
        JsonProvider provider = JsonProvider.provider();
        JsonBuilderFactory factory = provider.createBuilderFactory(config);
        JsonArrayBuilder aBuilder = factory.createArrayBuilder();
        aBuilder.add("hello");
        aBuilder.add("world");
        aBuilder.add(factory.createObjectBuilder().add("name", "earth"));
        JsonArray jsonObject = aBuilder.build();
        System.out.println(jsonObject.toString());
        //Writer writer = new OutputStreamWriter(System.out);
        StringWriter writer = new StringWriter();
        JsonWriterFactory wfactory = Json.createWriterFactory(config);
        JsonWriter jwriter = wfactory.createWriter(writer);
        jwriter.write(jsonObject);
        String expected = "[hello, world, {name=earth}]";
        JSONAssert.assertEquals(expected, writer.getBuffer().toString(), true);
    }

    @Test
    public void testReaderWriter() throws URISyntaxException, IOException, JSONException {
        JsonProvider provider = JsonProvider.provider();
        assertTrue(provider instanceof GraalsonProvider);
        URL test = GraalsonTest.class.getResource("/default.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        JsonObject jsonObject = reader.readObject();
        //Writer writer = new OutputStreamWriter(System.out);
        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);

        javax.script.Bindings result = JsonObjectBindings.from(jsonObject);
        System.out.println("");
        System.out.println("bindings --->" + result.toString() + "<---");
        String expected = new Scanner(GraalsonTest.class.getResourceAsStream("/default.json")).useDelimiter("\\Z").next();
        JSONAssert.assertEquals(expected, writer.getBuffer().toString(), true);
    }

    @Test
    public void testFactory() throws URISyntaxException, IOException, JSONException {
        Map config = null;
        JsonBuilderFactory factory = Json.createBuilderFactory(config);

        JsonObject jsonObject = factory.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", factory.createObjectBuilder()
                        .add("streetAddress", "21 2nd Street")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", factory.createArrayBuilder()
                        .add(factory.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(factory.createObjectBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();

        //Writer writer = new OutputStreamWriter(System.out);
        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);

        JsonObjectBindings result = JsonObjectBindings.from(jsonObject);
        System.out.println("");
        System.out.println("bindings --->" + result.toString() + "<---");
        System.out.println("bindings json --->" + result.stringify() + "<---");

        String expected = new Scanner(GraalsonTest.class.getResourceAsStream("/default.json")).useDelimiter("\\Z").next();
        JSONAssert.assertEquals(expected, writer.getBuffer().toString(), true);
    }

    @Test
    @Disabled
    public void testMergePatch() throws URISyntaxException, IOException, JSONException {

        JsonProvider provider = JsonProvider.provider();
        URL test = GraalsonTest.class.getResource("/default.json");
        URL test1 = GraalsonTest.class.getResource("/default.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        Reader scriptReader1 = Files.newBufferedReader(Paths.get(test1.toURI()));
        JsonReader reader1 = Json.createReader(scriptReader1);
        JsonObject jsonObject = reader.readObject();
        JsonObject jsonObject1 = reader1.readObject();

        JsonMergePatch mergePatch = provider.createMergeDiff(jsonObject, jsonObject1);

        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        assertNotNull(mergePatch);
        assertNotNull(mergePatch.toJsonValue());
        System.err.println(mergePatch.toJsonValue().toString());

        jwriter.write(mergePatch.toJsonValue());
    }

    @Test
    public void testDiff() throws URISyntaxException, IOException, JSONException {

        JsonProvider provider = JsonProvider.provider();
        URL test = GraalsonTest.class.getResource("/default.json");
        URL test1 = GraalsonTest.class.getResource("/default_1.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        Reader scriptReader1 = Files.newBufferedReader(Paths.get(test1.toURI()));
        JsonReader reader1 = Json.createReader(scriptReader1);
        JsonObject jsonObject = reader.readObject();
        JsonObject jsonObject1 = reader1.readObject();

        JsonPatch diff = provider.createDiff(jsonObject, jsonObject1);

        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        assertNotNull(diff);
        assertNotNull(diff.toJsonArray());
        assertNotNull(diff.toJsonArray().getFirst());
        jwriter.write(diff.toJsonArray());
    }

    @Test
    public void testDiffApply() throws Exception {

        JsonObject original;

        try (InputStream in = GraalsonTest.class.getResourceAsStream("/default.json"); JsonReader reader = Json.createReader(in)) {
            original = reader.readObject();
        }

        JsonArray patchArray;

        try (InputStream in = GraalsonTest.class.getResourceAsStream("/diff.json"); JsonReader reader = Json.createReader(in)) {
            patchArray = reader.readArray();
        }

        JsonPatch patch = Json.createPatch(patchArray);
        assertNotNull(patch.toJsonArray());
        assertNotNull(patch.toJsonArray().getFirst());

        JsonObject expected;

        try (InputStream in = GraalsonTest.class.getResourceAsStream("/default_1.json"); JsonReader reader = Json.createReader(in)) {
            expected = reader.readObject();
        }

        JsonObject patched = patch.apply(original);

        assertSame(expected, patched);
    }

    public static boolean assertSame(JsonValue v1, JsonValue v2) {
        if (v1 == v2) {
            return true;
        }
        if (v1 == null || v2 == null) {
            return false;
        }
        if (!v1.getValueType().equals(v2.getValueType())) {
            return false;
        }
        switch (v1.getValueType()) {
            case OBJECT:
                JsonObject o1 = (JsonObject) v1;
                JsonObject o2 = (JsonObject) v2;
                if (!o1.keySet().equals(o2.keySet())) {
                    return false;
                }
                for (String k : o1.keySet()) {
                    if (!assertSame(o1.get(k), o2.get(k))) {
                        return false;
                    }
                }
                return true;
            case ARRAY:
                JsonArray a1 = (JsonArray) v1;
                JsonArray a2 = (JsonArray) v2;
                if (a1.size() != a2.size()) {
                    return false;
                }
                for (int i = 0; i < a1.size(); i++) {
                    if (!assertSame(a1.get(i), a2.get(i))) {
                        return false;
                    }
                }
                return true;
            case STRING:
                Assertions.assertEquals(((JsonString) v1).getString(), ((JsonString) v2).getString());
                return true;
            case NUMBER:
                Assertions.assertEquals(((JsonNumber) v1).bigDecimalValue(), ((JsonNumber) v2).bigDecimalValue());
                return true;
            case TRUE:
            case FALSE:
                return v1.getValueType() == v2.getValueType();
            case NULL:
                return true;
            default:
                return false;
        }
    }
}
