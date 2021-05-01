package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.toValue;
import static au.com.devnull.graalson.GraalsonProvider.valueFor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonArrayBuilder implements JsonArrayBuilder {

    Value value = null;

    public GraalsonArrayBuilder() {
        this.value = valueFor(List.class);
    }

    @Override
    public JsonArrayBuilder add(JsonValue value) {
        switch (value.getValueType()) {
            case ARRAY:
                this.value.setArrayElement(this.value.getArraySize(), toValue(value));
                break;
            case OBJECT:
                this.value.setArrayElement(this.value.getArraySize(), toValue(value));
                break;
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case STRING:
                this.value.setArrayElement(this.value.getArraySize(), value);
        }
        return this;
    }

    @Override
    public JsonArrayBuilder add(String value) {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(BigDecimal value) {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(BigInteger value) {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(int value) {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(long value) {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(double value) {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(boolean value) {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder addNull() {
        this.value.setArrayElement(this.value.getArraySize(), value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(JsonObjectBuilder builder) {
        //TODO: evaluate build later?
        GraalsonObject o = (GraalsonObject) builder.build();
        this.value.setArrayElement(this.value.getArraySize(), o.getGraalsonValue());
        return this;
    }

    @Override
    public JsonArrayBuilder add(JsonArrayBuilder builder) {
        //TODO: evaluate build later?
        GraalsonArray o = (GraalsonArray) builder.build();
        this.value.setArrayElement(this.value.getArraySize(), o.getGraalsonValue());
        return this;
    }

    @Override
    public JsonArray build() {
        return new GraalsonArray(value);
    }

}
