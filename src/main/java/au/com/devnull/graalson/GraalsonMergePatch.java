package au.com.devnull.graalson;

import jakarta.json.JsonMergePatch;
import jakarta.json.JsonValue;

/**
 *
 * @author wozza
 */
@Deprecated
class GraalsonMergePatch implements JsonMergePatch {

    @Override
    public JsonValue apply(JsonValue target) {
        return null;
    }

    @Override
    public JsonValue toJsonValue() {
        return null;
    }

}
