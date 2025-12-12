package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.buildConfig;
import static au.com.devnull.graalson.GraalsonProvider.getPolyglotContext;
import static au.com.devnull.graalson.GraalsonStructure.valueFor;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * Provides an adapter between the json-api interface an a polyglot value
 * available to truffle
 *
 * @author wozza
 */
public abstract sealed class GraalsonValue implements JsonValue permits
        GraalsonBoolean,
        GraalsonNull,
        GraalsonNumber,
        GraalsonString,
        GraalsonStructure {

    protected Value value;

    abstract Value getGraalsonValue();

    public String toString() {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static GraalsonValue from(JsonValue value) {

        if (value instanceof GraalsonValue g) {
            return g;
        }

        switch (value) {
            case JsonArray v ->
                new GraalsonArray(v);
            case JsonNumber v ->
                GraalsonNumber.from(v);
            case JsonObject v ->
                new GraalsonObject(v);
            case JsonString v ->
                new GraalsonString(v.getString());
            default -> {
                switch (value.getValueType()) {
                    case NULL:
                        return new GraalsonNull();
                    case FALSE:
                        return new GraalsonBoolean(Boolean.FALSE);
                    case TRUE:
                        return new GraalsonBoolean(Boolean.TRUE);
                }
            }
        }
        throw new IllegalArgumentException(value.getClass() + " reported " + value.getValueType());
    }

    public static Value deepClonePolyglotValue(Value v) {
        Context ctx = GraalsonProvider.getPolyglotContext();
        if (v.isBoolean()) {
            return ctx.asValue(v.asBoolean());
        } else if (v.isNumber()) {
            return ctx.asValue(v.asDouble());
        } else if (v.isString()) {
            return ctx.asValue(v.asString());
        } else if (v.isNull()) {
            return ctx.eval("js", "null");
        } else if (v.hasArrayElements()) {
            Value arr = ctx.eval("js", "[]");
            long len = v.getArraySize();
            for (long i = 0; i < len; i++) {
                arr.setArrayElement(i, deepClonePolyglotValue(v.getArrayElement(i)));
            }
            return arr;
        } else if (v.hasMembers()) {
            Value obj = ctx.eval("js", "({})");
            for (String k : v.getMemberKeys()) {
                Value v2 = v.getMember(k);
                if (v2.isHostObject()) {
                    obj.putMember(k, v2);
                } else {
                    Value o2 = deepClonePolyglotValue(v2);
                    obj.putMember(k, o2);
                }
            }
            return obj;
        }
        return v;
    }

    public static GraalsonValue toJsonValue(Object o) {
        if (o instanceof List) {
            return new GraalsonArray((List) o);
        } else if (o instanceof Value v) {
            if (v.hasArrayElements()) {
                return new GraalsonArray(v);
            } else if (v.hasMembers()) {
                return new GraalsonObject(v);
            }
        }

        throw new IllegalArgumentException(o == null ? "null" : (o.getClass() + " " + o.toString()));
    }

    static GraalsonValue toJsonValue(Value o) {
        if (o == null) {
            return new GraalsonNull();
        }
        if (o.isHostObject() && (o.asHostObject() instanceof GraalsonStructure v)) {
            return v;
        }
        if (o.hasArrayElements()) {
            return new GraalsonArray(o);
        } else if (o.hasMembers()) {
            return new GraalsonObject(o);
        } else if (o.isNumber()) {
            return new GraalsonNumber(o);
        } else if (o.isString()) {
            return new GraalsonString(o);
        } else if (o.isBoolean()) {
            return new GraalsonBoolean(o);
        } else if (o.isNull()) {
            return new GraalsonNull();
        }

        throw new IllegalArgumentException(o == null ? "null" : (o.getClass() + " " + o.toString()));
    }

    public static Value toValue(JsonValue value) {
        if (value instanceof GraalsonStructure g) {
            return g.getGraalsonValue();
        } else if (value instanceof GraalsonValue g) {
            return g.getGraalsonValue();
        }

        throw new IllegalArgumentException(value.getValueType().toString());
    }

    static Value toValue(Object o) {
        return switch (o) {
            /**
             * case GraalsonObject g -> g.getGraalsonValue(); case GraalsonArray
             * g -> g.getGraalsonValue(); case GraalsonString g ->
             * g.getGraalsonValue(); case GraalsonNumber g ->
             * g.getGraalsonValue(); case GraalsonBoolean g ->
             * g.getGraalsonValue(); *
             */
            case Map map ->
                new GraalsonObject(map).getGraalsonValue();
            case Set set ->
                new GraalsonArray(set).getGraalsonValue();
            case List list ->
                new GraalsonArray(list).getGraalsonValue();
            case String s ->
                new GraalsonString(s).getGraalsonValue();
            case Number n ->
                new GraalsonNumber(n).getGraalsonValue();
            case Boolean b ->
                new GraalsonBoolean(b).getGraalsonValue();
            default ->
                throw new IllegalArgumentException(o == null ? "null" : (o.getClass() + " " + o.toString()));
        };
    }

    static <T extends JsonValue> T toJsonValue(Value o, Class<T> jClass) {

        if (jClass.equals(JsonObject.class)) {
            return (T) new GraalsonObject(o);
        } else if (jClass.equals(JsonArray.class)) {
            return (T) new GraalsonArray(o);
        } else if (jClass.equals(JsonNumber.class)) {
            return (T) new GraalsonNumber(o);
        } else if (jClass.equals(JsonString.class)) {
            return (T) new GraalsonString(o);
        }
        throw new IllegalArgumentException(o == null ? "null" : (o.getClass() + " " + o.toString()));
    }

    public static Object toJava(JsonValue value) {

        switch (value.getValueType()) {
            case NUMBER:
                return ((JsonNumber) value).intValue();
            case STRING:
                return ((JsonString) value).getString();
            case OBJECT:
                return GraalsonStructure.toJava((JsonObject) value);
            case ARRAY:
                return GraalsonStructure.toJava((JsonArray) value);
            case FALSE:
                return ((GraalsonBoolean) value).getBoolean();
            case NULL:
                return null;
        }
        throw new IllegalArgumentException(value.toString());
    }

    static Value jsonParse(String value) {
        return GraalsonProvider.getPolyglotContext().eval("js", "value= " + value);
    }

    static String jsonStringify(Value context) {
        getPolyglotContext().getBindings("js").putMember("mine", context);
        String script = MessageFormat.format("result = JSON.stringify(mine,{0},{1})", (Object[]) buildConfig());
        getPolyglotContext().eval("js", script);
        Value result = getPolyglotContext().getBindings("js").getMember("result");
        return result.toString();
    }

    static String jsonStringify(Set<Entry<String, Object>> entrySet) {
        return jsonStringify(entrySet, Collections.EMPTY_MAP);
    }

    static String jsonStringify(Set<Entry<String, Object>> entrySet, Map<String, Object> config) {
        Value map = valueFor(Map.class);
        entrySet.forEach(e -> map.putMember(e.getKey(), e.getValue()));
        return jsonStringify(map);
    }

}
