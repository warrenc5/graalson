package au.com.devnull.graalson;

import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

/**
 * roughly implements RFC7369 JSON Merge Patch
 * http://tools.ietf.org/html/rfc7396
 *
 * @author wozza
 */
public final class GraalsonMergePatch implements JsonMergePatch {

    JsonValue value = null;

    public GraalsonMergePatch(JsonValue mergePatch) {
        this.value = mergePatch;
    }

    @Override
    public JsonValue apply(JsonValue target) {
        return null;
    }

    @Override
    public JsonValue toJsonValue() {
        return value;
    }

    public String toString() {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static JsonStructure createDiff(JsonObject src, JsonObject dest) {
        //iterate src keys
        
        //if value is object then walk recurse and walk into object
        return null;
    }

}
