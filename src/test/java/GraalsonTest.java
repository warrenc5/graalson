
import au.com.devnull.graalson.JsonObjectBindings;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        URL test = GraalsonTest.class.getResource("/rvn.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        JsonObject jsonObject = reader.readObject();
        javax.script.Bindings result = (Bindings) new JsonObjectBindings(jsonObject);
        System.out.println(result.toString());

        Writer writer = new OutputStreamWriter(System.out);
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);

    }

}
