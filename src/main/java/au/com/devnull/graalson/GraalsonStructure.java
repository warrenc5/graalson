package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.getPolyglotContext;
import static au.com.devnull.graalson.GraalsonValue.toJsonValue;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public abstract sealed class GraalsonStructure extends GraalsonValue implements JsonStructure permits GraalsonArray, GraalsonObject {

    public static final Class<List> ARRAY_CLASS = List.class;
    public static final Class<Map> OBJECT_CLASS = Map.class;

    public ValueType getValueType() {
        return getValue().getValueType();
    }

    JsonStructure getValue() {
        return this;
    }

    abstract <T extends JsonStructure> T execute(GraalsonPatch.Step step);

    abstract GraalsonStructure deepClone();

    static Value valueFor(Class<? extends Object> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            //Map<Object, Object> map = getPolyglotContext().eval("js", "{}").as(Map.class);
            //assert ((Map<Object, Object>) getPolyglotContext().eval("js", "[{}]").as(Object.class)).get(0) instanceof Map;
            //FIXME: assert fails, map is null
            //return Value.asValue(ProxyObject.fromMap(new HashMap())); //
            return getPolyglotContext().getBindings("js").getMember("Object").execute();
        } else if (List.class
                .isAssignableFrom(clazz)) {
            List<Object> list = getPolyglotContext().eval("js", "[]").as(List.class);

            return Value.asValue(list);
        }
        throw new IllegalArgumentException(clazz.getCanonicalName());
    }

    static void copyInto(Map<String, JsonValue> o, Value value) {
        o.entrySet().forEach(e -> value.putMember(e.getKey(), toJava(e.getValue())));
    }

    static void copyInto(Set<JsonValue> o, Value value) {
        o.forEach(e -> value.setArrayElement(value.getArraySize(), toJava(e)));
    }

    static void copyInto(List<JsonValue> o, Value value) {
        o.forEach(e -> value.setArrayElement(value.getArraySize(), toJava(e)));
    }

    /**
     * Copy the members from the Polyglot Value into the Java Map
     *
     * @param o
     * @param javaMap
     */
    static void copyInto(Value o, Map javaMap) {
        if (o.hasMembers()) {
            for (String key : o.getMemberKeys()) {

                Value value = o.getMember(key);

                JsonValue jsonValue = null;

                if (value.isHostObject() && value.asHostObject() instanceof JsonValue v) {
                    javaMap.put(key, v);
                } else {
                    jsonValue = toJsonValue((Value) value);
                    javaMap.put(key, jsonValue);
                }
            }
        }
    }

    static List toJava(JsonArray value) {
        List result = new ArrayList();
        for (int i = 0; i < value.size(); i++) {
            Object v = toJava(value.get(i));
            result.add(v);
        }
        return result;
    }

    static Map toJava(JsonObject value) {
        Map result = new HashMap();
        for (Entry<String, JsonValue> e : value.entrySet()) {
            Object v = toJava(e.getValue());
            result.put(e.getKey(), v);
        }
        return result;
    }
}
