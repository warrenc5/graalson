package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.toJsonValue;
import java.util.HashMap;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonObject extends HashMap<String, JsonValue> implements JsonObject, GraalsonValue {

    public Value value = null;

    public GraalsonObject(Value value) {
        this.value = value;
        Set<String> keys = value.getMemberKeys();
        //for (Value k = null; keys.hasIteratorNextElement(); k = keys.getIteratorNextElement()) {
        for (String k : keys) {
            Object member = value.getMember(k);

            JsonValue jValue = null;

            //if ("address".equals(k)) {
            if (member instanceof Value) {
                if (((Value) member).isHostObject()) {
                    member = ((Value) member).asHostObject();
                }
            }
            if (member instanceof JsonValue) {
                jValue = (JsonValue) member;
            } else if (member instanceof GraalsonValue) {
                member = ((GraalsonValue) member).getGraalsonValue();
                jValue = (JsonValue) member;
            } else {
                jValue = toJsonValue((Value) member);
            }
            this.put(k, jValue);

        }
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
