package au.com.devnull.graalson;

import jakarta.json.JsonString;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public final class GraalsonString extends GraalsonValue implements JsonString {

    public GraalsonString(String value) {
        this(Value.asValue(value));
    }

    public GraalsonString(Value value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return value.asString();
    }

    @Override
    public CharSequence getChars() {
        return value.asString();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public Value getGraalsonValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.asString();
    }

}
