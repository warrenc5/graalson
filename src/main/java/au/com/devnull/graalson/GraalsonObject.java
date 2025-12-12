package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonStructure.copyInto;
import static au.com.devnull.graalson.GraalsonStructure.OBJECT_CLASS;
import static au.com.devnull.graalson.GraalsonStructure.valueFor;
import static au.com.devnull.graalson.GraalsonValue.deepClonePolyglotValue;
import static au.com.devnull.graalson.GraalsonValue.toJsonValue;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import static jakarta.json.JsonPatch.Operation.ADD;
import static jakarta.json.JsonPatch.Operation.COPY;
import static jakarta.json.JsonPatch.Operation.MOVE;
import static jakarta.json.JsonPatch.Operation.REMOVE;
import static jakarta.json.JsonPatch.Operation.REPLACE;
import static jakarta.json.JsonPatch.Operation.TEST;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.graalvm.polyglot.Value;
import java.util.Map.Entry;
import java.util.AbstractMap;
import java.util.stream.Collectors;

/**
 * Provides a JsonObject backed by a Map all Map operations
 *
 * @author wozza
 */
@SuppressWarnings("unchecked")
public final class GraalsonObject extends GraalsonStructure implements JsonObject {

    public GraalsonObject(Map<String, JsonValue> o) {
        value = valueFor(OBJECT_CLASS);
        putAll(o);
    }

    public GraalsonObject(Value o) {
        this.value = o;
    }

    @Override
    public JsonArray getJsonArray(String name) {
        return toJsonValue(value.getMember(name), JsonArray.class);
    }

    @Override
    public JsonObject getJsonObject(String name) {
        return toJsonValue(value.getMember(name), JsonObject.class);
    }

    @Override
    public JsonNumber getJsonNumber(String name) {
        return toJsonValue(value.getMember(name), JsonNumber.class);
    }

    @Override
    public JsonString getJsonString(String name) {
        return toJsonValue(value.getMember(name), JsonString.class);
    }

    @Override
    public String getString(String name) {
        return (value.getMember(name)).asString();
    }

    @Override
    public String getString(String name, String defaultValue) {
        Value member = value.getMember(name);
        if (member == null || member.isNull()) {
            return defaultValue;
        }
        return member.asString();
    }

    @Override
    public int getInt(String name) {
        return value.getMember(name).asInt();
    }

    @Override
    public int getInt(String name, int defaultValue) {
        return value.getMember(name).asInt();
    }

    @Override
    public boolean getBoolean(String name) {
        return value.getMember(name).asBoolean();
    }

    @Override
    public boolean getBoolean(String name, boolean defaultValue) {
        return value.getMember(name).asBoolean();
    }

    @Override
    public boolean isNull(String name) {
        return value.getMember(name).isNull();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }

    @Override
    public Value getGraalsonValue() {
        return value;
    }

    public GraalsonObject deepClone() {
        return new GraalsonObject(deepClonePolyglotValue(this.value));
    }

    @Override
    public <T extends JsonStructure> T execute(GraalsonPatch.Step step) {
        switch (step.op) {
            case ADD:

                this.asJsonObject().put(
                        step.pointer.lastPathComponent(), step.value);
                break;

            case REMOVE:

                this.asJsonObject().remove(step.pointer.lastPathComponent());
                break;
            case REPLACE:
                this.asJsonObject().put(
                        step.pointer.lastPathComponent(), step.value);
                break;
            case COPY:
                break;
            case MOVE:
                break;
            case TEST:
                break;
            default:
        }
        return (T) this;
    }

    @Override
    public JsonValue put(String key, JsonValue j) {
        //Value oldMember = this.value.getMember(key);
        value.putMember(key, toJava(j));
        return null; //toJsonValue(oldMember);
    }

    @Override
    public int size() {
        return value.getMemberKeys().size();
    }

    @Override
    public boolean isEmpty() {
        return value.getMemberKeys().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return value.hasMember((String) key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JsonValue get(Object key) {
        return toJsonValue(value.getMember((String) key));
    }

    @Override
    public JsonValue remove(Object key) {
        Value v = this.value.getMember((String) key);
        if (v != null) {
            this.value.removeMember((String) key);
        }
        return toJsonValue(v);
    }

    public void putAll(Map<? extends String, ? extends JsonValue> m) {
        for (Map.Entry<? extends String, ? extends JsonValue> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (String k : value.getMemberKeys()) {
            value.removeMember(k);
        }
    }

    @Override
    public Set<String> keySet() {
        return value.getMemberKeys();
    }

    @Override
    public Collection<JsonValue> values() {
        return value.getMemberKeys().stream().map(k -> toJsonValue(value.getMember(k))).collect(toList());
    }

    public Set<Entry<String, JsonValue>> entrySet() {
        return value.getMemberKeys().stream()
                .map(k -> new AbstractMap.SimpleEntry<String, JsonValue>(k, toJsonValue(value.getMember(k))))
                .collect(Collectors.toSet());
    }

}
