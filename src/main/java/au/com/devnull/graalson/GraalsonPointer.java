package au.com.devnull.graalson;

import au.com.devnull.graalson.GraalsonPatch.Step;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch.Operation;
import jakarta.json.JsonPointer;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

/**
 * roughly implements RFC6901 JavaScript Object Notation (JSON) Pointer
 * https://datatracker.ietf.org/doc/html/rfc6901
 *
 * @author wozza
 */
class GraalsonPointer implements JsonPointer {

    final String path;

    GraalsonPointer(String path) {
        this.path = path;
    }

    @Override
    public <T extends JsonStructure> T add(T target, JsonValue value) {
        return locate(target).execute(new Step(Operation.ADD, path, value));
    }

    @Override
    public <T extends JsonStructure> T remove(T target) {
        return locate(target).execute(new Step(Operation.REMOVE, path));
    }

    @Override
    public <T extends JsonStructure> T replace(T target, JsonValue value) {
        return locate(target).execute(new Step(Operation.REPLACE, path, value));
    }

    @Override
    public boolean containsValue(JsonStructure target) {
        return target.getValue(path) != null;
    }

    public GraalsonPointer deriveParentPath() {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return new GraalsonPointer("");
        }
        return new GraalsonPointer(path.substring(0, lastSlash));
    }

    String lastPathComponent() {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1 || lastSlash == path.length() - 1) {
            return "";
        }
        String component = path.substring(lastSlash + 1);
        return component.replace("~1", "/").replace("~0", "~");
    }

    int lastPathComponentIndex() {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1 || lastSlash == path.length() - 1) {
            return -1;
        }
        String component = path.substring(lastSlash + 1).replace("~1", "/").replace("~0", "~");
        try {
            return Integer.parseInt(component);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public JsonValue getValue(JsonStructure target) {
        String[] tokens = path.split("/");
        Object current = target;
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].replace("~1", "/").replace("~0", "~");
            if (current instanceof JsonObject obj) {
                current = obj.get(token);
            } else if (current instanceof JsonArray arr) {
                int idx = Integer.parseInt(token);
                current = arr.get(idx);
            } else {
                throw new IllegalStateException("Unable to traverse non-container at segment: " + token);
            }
        }
        if (current instanceof JsonValue) {
            return (JsonValue) current;
        }
        throw new IllegalStateException("Target is not a JsonValue at the end of pointer: " + path);
    }

    public GraalsonStructure locate(JsonStructure target) {
        return (GraalsonStructure) this.deriveParentPath().getValue(target);
    }

}
