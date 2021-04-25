package au.com.devnull.graalson;

import javax.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonBoolean implements JsonValue, GraalsonValue {

    private final Value value;

    public GraalsonBoolean(Value o) {
        this.value = o;
    }

    public Boolean getBoolean() {
        return this.value.asBoolean();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.FALSE;
    }

    @Override
    public Value getGraalsonValue() {
        return value;
    }

}
