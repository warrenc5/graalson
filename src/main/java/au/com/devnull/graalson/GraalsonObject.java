package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.copyInto;
import static au.com.devnull.graalson.GraalsonProvider.toJsonValue;
import static au.com.devnull.graalson.GraalsonProvider.valueFor;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonObject extends LinkedHashMap<String, JsonValue> implements JsonObject, GraalsonValue {

    public Value value = null;

    public GraalsonObject(Map o) {
        this(valueFor(Map.class));
        copyInto(o, value);
    }

    public GraalsonObject(Value o) {
        this.value = o;
        copyInto(o, this);
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
        return value.getMember(name).asString();
    }

    @Override
    public String getString(String name, String defaultValue) {
        return value.getMember(name).asString();
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

}
