package au.com.devnull.graalson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.spi.JsonProvider;
import org.junit.Test;

/**
 *
 * @author wozza
 */
public class GraalsonTest {

    @Test
    public void testBuilder() {

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
        Writer writer = new OutputStreamWriter(System.out);
        JsonWriterFactory wfactory = Json.createWriterFactory(config);
        JsonWriter jwriter = wfactory.createWriter(writer);
        jwriter.write(jsonObject);
    }

    @Test
    public void testReaderWriter() throws URISyntaxException, IOException {
        JsonProvider provider = JsonProvider.provider();
        URL test = GraalsonTest.class.getResource("/default.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        JsonObject jsonObject = reader.readObject();
        Writer writer = new OutputStreamWriter(System.out);
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);

        javax.script.Bindings result = JsonObjectBindings.from(jsonObject);
        System.out.println("");
        System.out.println("bindings --->" + result.toString() + "<---");

    }

    @Test
    public void testFactory() throws URISyntaxException, IOException {
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

        Writer writer = new OutputStreamWriter(System.out);
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);

        JsonObjectBindings result = JsonObjectBindings.from(jsonObject);
        System.out.println("");
        System.out.println("bindings --->" + result.toString() + "<---");
        System.out.println("bindings json --->" + result.stringify() + "<---");

    }

}
