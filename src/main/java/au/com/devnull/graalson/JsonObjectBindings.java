package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.toJava;
import java.util.HashMap;
import java.util.Map;
import jakarta.json.JsonObject;
import javax.script.SimpleBindings;

/**
 *
 * @author wozza
 */
public class JsonObjectBindings extends SimpleBindings {

    public JsonObjectBindings(Map<String, Object> bindings) {
        super(bindings);
    }

    public JsonObjectBindings(JsonObject jsonObject) {
        super(new HashMap<String, Object>());
        super.putAll(toJava(jsonObject));
    }

    public static JsonObjectBindings from(JsonObject jsonObject) {
        return new JsonObjectBindings(jsonObject);
    }

    @Override
    public String toString() {
        return super.entrySet().toString();
    }

    public String stringify() {
        return GraalsonProvider.jsonStringify(super.entrySet());
    }
}
