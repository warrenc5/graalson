package au.com.devnull.graalson;

import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonBoolean implements GraalsonValue {

    private final Value value;

    public GraalsonBoolean(Boolean o) {
        this(Value.asValue(o));
    }

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
