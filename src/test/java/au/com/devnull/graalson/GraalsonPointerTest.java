package au.com.devnull.graalson;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.spi.JsonProvider;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraalsonPointerTest {

    @Test
    public void testAddOperationOnJsonObject() {
        JsonObject target = Json.createObjectBuilder()
                .add("name", "John")
                .add("age", 30)
                .build();

        JsonPatch patch = JsonProvider.provider().createPatchBuilder().add("/city", "Sydney").build();
        JsonObject updated = patch.apply(target);
        assertNotNull(updated);
        assertEquals("John", updated.getString("name"));
        assertEquals(30, updated.getInt("age"));
        assertEquals("Sydney", updated.getString("city"));
    }

    @Test
    public void testAddOperationOnJsonArray() {
        JsonArray target = Json.createArrayBuilder()
                .add("alpha")
                .add("beta")
                .build();

        JsonPatch patch = JsonProvider.provider().createPatchBuilder().add("/1", "gamma").build();

        assertTrue(patch.toJsonArray() instanceof JsonArray);
        assertNotNull(patch.toJsonArray().getFirst());

        JsonArray updated = patch.apply(target);
        assertNotNull(updated);
        System.out.println(Arrays.asList(((GraalsonArray) updated).toArray()).toString());
        assertEquals(3, updated.size());
        assertEquals("alpha", updated.getString(0));
        assertEquals("gamma", updated.getString(1));
        assertEquals("beta", updated.getString(2));
    }
}
