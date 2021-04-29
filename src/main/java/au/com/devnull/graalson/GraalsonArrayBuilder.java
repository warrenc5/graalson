package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonGenerator.valueFor;
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
                break;
            case OBJECT:
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
        GraalsonObject value = (GraalsonObject) builder.build();

        this.value.setArrayElement(this.value.getArraySize(), value.getGraalsonValue());
        return this;
    }

    @Override
    public JsonArrayBuilder add(JsonArrayBuilder builder) {
        GraalsonArray value = (GraalsonArray) builder.build();
        this.value.setArrayElement(this.value.getArraySize(), value.getGraalsonValue());
        return this;
    }

    @Override
    public JsonArray build() {
        return new GraalsonArray(value);
    }

}
