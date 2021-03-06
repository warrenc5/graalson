package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.valueFor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import static javax.json.JsonValue.ValueType.ARRAY;
import org.graalvm.polyglot.Value;
import static au.com.devnull.graalson.GraalsonProvider.toValue;

/**
 *
 * @author wozza
 */
public class GraalsonObjectBuilder implements JsonObjectBuilder {

    Value value = null;

    public GraalsonObjectBuilder() {
        this.value = valueFor(Map.class);
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
}
