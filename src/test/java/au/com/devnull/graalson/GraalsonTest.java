package au.com.devnull.graalson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.spi.JsonProvider;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

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

}
