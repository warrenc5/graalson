package au.com.devnull.graalson;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class GraalsonWriterTest {

    @Test
    public void testWriteSimpleJsonObject() {
        JsonObjectBuilder builder = new GraalsonObjectBuilder();
        builder.add("foo", "bar");
        builder.add("num", 1);
        JsonObject obj = builder.build();
        StringWriter sw = new StringWriter();
        GraalsonGenerator gen = new GraalsonGenerator(sw);
        JsonWriter writer = gen.gwriter;
        writer.writeObject(obj);
        String result = sw.toString();
        System.out.println(result);
        Assertions.assertTrue(result.contains("foo"));
        Assertions.assertTrue(result.contains("bar"));
        Assertions.assertTrue(result.contains("num"));
        Assertions.assertTrue(result.contains("1"));
    }

    @Test
    public void testWriteSimpleJsonArray() {
        JsonArrayBuilder builder = new GraalsonArrayBuilder();
        builder.add("hello");
        builder.add("world");
        JsonArray arr = builder.build();
        StringWriter sw = new StringWriter();
        GraalsonGenerator gen = new GraalsonGenerator(sw);
        JsonWriter writer = gen.gwriter;
        writer.writeArray(arr);
        String result = sw.toString();
        System.out.println(result);
        Assertions.assertTrue(result.contains("hello"));
        Assertions.assertTrue(result.contains("world"));
    }

    @Test
    public void testWriteJsonObjectContainingJsonArray() {
        JsonArrayBuilder arrayBuilder = new GraalsonArrayBuilder();
        arrayBuilder.add("a");
        arrayBuilder.add("b");
        JsonArray arr = arrayBuilder.build();

        JsonObjectBuilder objectBuilder = new GraalsonObjectBuilder();
        objectBuilder.add("list", arr);
        objectBuilder.add("foo", "bar");
        JsonObject obj = objectBuilder.build();

        StringWriter sw = new StringWriter();
        GraalsonGenerator gen = new GraalsonGenerator(sw);
        JsonWriter writer = gen.gwriter;
        writer.writeObject(obj);
        String result = sw.toString();
        System.out.println(result);
        Assertions.assertTrue(result.contains("\"list\""));
        Assertions.assertTrue(result.contains("["));
        Assertions.assertTrue(result.contains("\"a\""));
        Assertions.assertTrue(result.contains("\"b\""));
        Assertions.assertTrue(result.contains("\"foo\""));
        Assertions.assertTrue(result.contains("\"bar\""));
    }

    @Test
    public void testToString() throws JSONException {
        JsonReader reader = Json.createReader(ClassLoader.getSystemResourceAsStream("default.json"));
        String expected = new Scanner(ClassLoader.getSystemResourceAsStream("default.json")).useDelimiter("\\Z").next();

        JsonObject actual = reader.readObject();

        assertNotNull(actual);
        assertNotNull(actual.toString());

        System.out.println(actual);

        JSONAssert.assertEquals(expected, actual.toString(), JSONCompareMode.STRICT_ORDER);
    }

    @Test
    public void testWriterConfig() throws JSONException {
        JsonReader reader = Json.createReader(ClassLoader.getSystemResourceAsStream("default.json"));

        JsonObject actual = reader.readObject();
        GraalsonProvider provider = (GraalsonProvider) GraalsonProvider.provider();
        Map config = provider.getConfigInUse();
        config.put("spaces", 4);
        System.out.println(Arrays.asList(provider.buildConfig()));
        System.out.println(actual);
    }

}
