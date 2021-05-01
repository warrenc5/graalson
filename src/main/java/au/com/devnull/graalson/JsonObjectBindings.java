package au.com.devnull.graalson;

import java.util.HashMap;
import javax.json.JsonObject;
import javax.script.SimpleBindings;
import static au.com.devnull.graalson.GraalsonProvider.toJava;

/**
 *
 * @author wozza
 */
public class JsonObjectBindings extends SimpleBindings {

    public JsonObjectBindings(JsonObject jsonObject) {
        super(new HashMap<String, Object>());
        super.putAll(toJava(jsonObject));
    }

    public static SimpleBindings from(JsonObject jsonObject ) {
        return new JsonObjectBindings(jsonObject);
    }

    @Override
    public String toString() {
        return super.entrySet().toString();
    }



}
