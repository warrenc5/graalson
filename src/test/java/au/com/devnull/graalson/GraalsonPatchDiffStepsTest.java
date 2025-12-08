package au.com.devnull.graalson;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraalsonPatchDiffStepsTest {

    @Test
    public void testAddJsonObject() {
        JsonObject source = Json.createObjectBuilder()
                .build();
        JsonObject nested = Json.createObjectBuilder()
                .add("city", "NY")
                .add("zip", 10001)
                .build();
        JsonObject target = Json.createObjectBuilder()
                .add("address", nested)
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.ADD, stepList.get(0).op);
        assertEquals("/address", stepList.get(0).pointer.path);
        assertTrue(stepList.get(0).value instanceof JsonObject);
        JsonObject valueObj = (JsonObject) stepList.get(0).value;
        assertEquals("NY", valueObj.getString("city"));
        assertEquals(10001, valueObj.getInt("zip"));
    }

    @Test
    public void testRemoveJsonObject() {
        JsonObject source = Json.createObjectBuilder()
                .add("name", "John")
                .add("age", 30)
                .build();
        JsonObject target = Json.createObjectBuilder()
                .add("age", 30)
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.REMOVE, stepList.get(0).op);
        assertEquals("/name", stepList.get(0).pointer.path);
    }

    @Test
    public void testReplaceJsonObject() {
        JsonObject source = Json.createObjectBuilder()
                .add("name", "John")
                .add("age", 30)
                .build();
        JsonObject target = Json.createObjectBuilder()
                .add("name", "Jim")
                .add("age", 30)
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.REPLACE, stepList.get(0).op);
        assertEquals("/name", stepList.get(0).pointer.path);
        assertEquals("Jim", stepList.get(0).value.toString().replaceAll("\"", ""));
    }

    @Test
    public void testAddElementToJsonArray() {
        JsonArray source = Json.createArrayBuilder()
                .add(1)
                .add(2)
                .build();
        JsonArray target = Json.createArrayBuilder()
                .add(1)
                .add(2)
                .add(3)
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.ADD, stepList.get(0).op);
        assertEquals("/2", stepList.get(0).pointer.path.replaceFirst("^/+", "/"));
        assertTrue(stepList.get(0).value instanceof jakarta.json.JsonNumber);
        assertEquals(3, ((jakarta.json.JsonNumber) stepList.get(0).value).intValue());
    }

    @Test
    public void testRemoveElementFromJsonArray() {
        JsonArray source = Json.createArrayBuilder()
                .add(1)
                .add(2)
                .add(3)
                .build();
        JsonArray target = Json.createArrayBuilder()
                .add(1)
                .add(2)
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.REMOVE, stepList.get(0).op);
        assertEquals("/2", stepList.get(0).pointer.path.replaceFirst("^/+", "/"));
    }

    @Test
    public void testReplaceElementInJsonArray() {
        JsonArray source = Json.createArrayBuilder()
                .add(1)
                .add(2)
                .add(3)
                .build();
        JsonArray target = Json.createArrayBuilder()
                .add(1)
                .add(7)
                .add(3)
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.REPLACE, stepList.get(0).op);
        assertEquals("/1", stepList.get(0).pointer.path.replaceFirst("^/+", "/"));
        assertTrue(stepList.get(0).value instanceof jakarta.json.JsonNumber);
        assertEquals(7, ((jakarta.json.JsonNumber) stepList.get(0).value).intValue());
    }

    @Test
    public void testAddKeyToJsonObject() {
        JsonObject source = Json.createObjectBuilder()
                .add("name", "John")
                .build();
        JsonObject target = Json.createObjectBuilder()
                .add("name", "John")
                .add("age", 30)
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.ADD, stepList.get(0).op);
        assertEquals("/age", stepList.get(0).pointer.path);
    }

    @Test
    public void testRemoveKeyFromJsonObject() {
        JsonObject source = Json.createObjectBuilder()
                .add("name", "John")
                .add("age", 30)
                .build();
        JsonObject target = Json.createObjectBuilder()
                .add("name", "John")
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.REMOVE, stepList.get(0).op);
        assertEquals("/age", stepList.get(0).pointer.path);
    }

    @Test
    public void testReplaceKeyInJsonObject() {
        JsonObject source = Json.createObjectBuilder()
                .add("firstName", "John")
                .build();
        JsonObject target = Json.createObjectBuilder()
                .add("firstName", "Jim")
                .build();
        JsonPatch patch = GraalsonPatch.createDiff(source, target);
        assertNotNull(patch);
        var stepList = ((GraalsonPatch) patch).steps;
        assertEquals(1, stepList.size());
        assertEquals(GraalsonPatch.Operation.REPLACE, stepList.get(0).op);
        assertEquals("/firstName", stepList.get(0).pointer.path);
        assertEquals("Jim", stepList.get(0).value.toString().replaceAll("\"", ""));
    }
}
