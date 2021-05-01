package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.copyInto;
import static au.com.devnull.graalson.GraalsonProvider.valueFor;
import java.util.AbstractList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import org.graalvm.polyglot.Value;
import static au.com.devnull.graalson.GraalsonProvider.toJsonValue;

/**
 *
 * @author wozza
 */
public class GraalsonArray extends AbstractList<JsonValue> implements JsonArray, GraalsonValue {

    Value value = null;

    public GraalsonArray(List o) {
        this(valueFor(List.class));
        copyInto(o, value);
    }

    public GraalsonArray(Value value) {
        this.value = value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }

    @Override
    public JsonValue get(int index) {
        return toJsonValue(value.getArrayElement(index));
    }

    @Override
    public int size() {
        return (int) value.getArraySize();
    }

    @Override
    public JsonObject getJsonObject(int index) {
        return toJsonValue(value.getArrayElement(index), JsonObject.class);
    }

    @Override
    public JsonArray getJsonArray(int index) {
        return toJsonValue(value.getArrayElement(index), JsonArray.class);
    }

    @Override
    public JsonNumber getJsonNumber(int index) {
        return toJsonValue(value.getArrayElement(index), JsonNumber.class);
    }

    @Override
    public JsonString getJsonString(int index) {
        return toJsonValue(value.getArrayElement(index), JsonString.class);
    }

    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getString(int index) {
        return value.getArrayElement(index).asString();
    }

    @Override
    public String getString(int index, String defaultValue) {
        return value.getArrayElement(index).asString();
    }

    @Override
    public int getInt(int index) {
        return value.getArrayElement(index).asInt();
    }

    @Override
    public int getInt(int index, int defaultValue) {
        return value.getArrayElement(index).asInt();
    }

    @Override
    public boolean getBoolean(int index) {
        return value.getArrayElement(index).asBoolean();
    }

    @Override
    public boolean getBoolean(int index, boolean defaultValue) {
        return value.getArrayElement(index).asBoolean();
    }

    @Override
    public boolean isNull(int index) {
        return value.getArrayElement(index).isNull();
    }

    @Override
    public Value getGraalsonValue() {
        return this.value;
    }
}
