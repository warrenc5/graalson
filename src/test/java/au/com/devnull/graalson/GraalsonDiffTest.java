package au.com.devnull.graalson;

import au.com.devnull.graalson.GraalsonPatch.Step;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraalsonDiffTest {

    @Test
    public void testDiffAddJsonObject() {
        JsonObject jsonObject1 = Json.createObjectBuilder()
                .add("name", "John")
                .add("age", 30)
                .build();

        JsonObject jsonObject2 = Json.createObjectBuilder()
                .add("name", "Jim")
                .add("age", 31)
                .build();

        JsonPatch diff = Json.createDiff(jsonObject1, jsonObject2);

        assertNotNull(diff);
        assertNotNull(diff.toJsonArray());
        assertNotNull(diff.toJsonArray().getFirst());
        assertEquals(2, diff.toJsonArray().size());

        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(diff.toJsonArray());
        
 
    }
}
