package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonValue.toValue;
import static au.com.devnull.graalson.GraalsonStructure.valueFor;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.ARRAY;
import static jakarta.json.JsonValue.ValueType.FALSE;
import static jakarta.json.JsonValue.ValueType.NULL;
import static jakarta.json.JsonValue.ValueType.NUMBER;
import static jakarta.json.JsonValue.ValueType.OBJECT;
import static jakarta.json.JsonValue.ValueType.STRING;
import static jakarta.json.JsonValue.ValueType.TRUE;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
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

    public GraalsonArrayBuilder(Collection<?> collection) {
        super();
        collection.forEach(o -> value.setArrayElement(this.value.getArraySize(), o));
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

    //Extra implementation for json-path
    public GraalsonArrayBuilder remove(int index) {
        this.value.removeArrayElement(index);
        return this;
    }

    public GraalsonArrayBuilder add(int index, JsonValue element) {
        this.value.setArrayElement(index, element); //FIXME: implement insert
        return this;
    }

    public GraalsonArrayBuilder set(int index, JsonValue element) {
        this.value.setArrayElement(index, element);
        return this;
    }

}
