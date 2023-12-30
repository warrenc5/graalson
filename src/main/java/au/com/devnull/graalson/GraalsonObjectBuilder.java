package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.toValue;
import static au.com.devnull.graalson.GraalsonProvider.valueFor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.ARRAY;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonObjectBuilder implements JsonObjectBuilder {

    Value value = null;

    public GraalsonObjectBuilder() {
        this.value = valueFor(Map.class);
    }

    GraalsonObjectBuilder(Map<String, Object> map) {
        super();
        map.forEach((k, v) -> value.putMember(k, v));
    }

    GraalsonObjectBuilder(JsonObject obj) {
        this.value = toValue(obj);
    }

    @Override
    public JsonObjectBuilder add(String name, JsonValue value) {
        switch (value.getValueType()) {
            case ARRAY:
                this.value.putMember(name, toValue(value));
                break;
            case OBJECT:
                this.value.putMember(name, toValue(value));
                break;
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case STRING:
                this.value.putMember(name, value);
        }
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, String value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigInteger value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigDecimal value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, int value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, long value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, double value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, boolean value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder addNull(String name) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        //TODO: evaluate build later?
        this.value.putMember(name, builder.build());
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        //TODO: evaluate build later?
        this.value.putMember(name, builder.build());
        return this;
    }

    @Override
    public JsonObject build() {
        GraalsonObject o = new GraalsonObject(value);
        return o;
    }

    //Extra implementation for json-path
    public GraalsonObjectBuilder remove(String toString) {
        return this;
    }
}
