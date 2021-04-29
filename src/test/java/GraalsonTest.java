
import au.com.devnull.graalson.JsonObjectBindings;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.spi.JsonProvider;
import javax.script.Bindings;
import org.junit.Test;

/**
 *
 * @author wozza
 */
public class GraalsonTest {

    @Test
    public void testBuilder() {
        JsonProvider provider = JsonProvider.provider();
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder aBuilder = factory.createArrayBuilder();
        aBuilder.add("hello");
        aBuilder.add("world");
        JsonArray jsonObject = aBuilder.build();

        System.out.println(jsonObject.toString());
        Writer writer = new OutputStreamWriter(System.out);
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);
    }

    @Test
    public void testReaderWriter() throws URISyntaxException, IOException {
        JsonProvider provider = JsonProvider.provider();
        URL test = GraalsonTest.class.getResource("/default.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        JsonObject jsonObject = reader.readObject();
        javax.script.Bindings result = (Bindings) new JsonObjectBindings(jsonObject);
        Writer writer = new OutputStreamWriter(System.out);
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);

    }

    @Test
    public void testFactory() throws URISyntaxException, IOException {
        Map config = null;
        JsonBuilderFactory factory = Json.createBuilderFactory(config);

        JsonObject value = factory.createObjectBuilder()
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
        jwriter.write(value);

    }

}
