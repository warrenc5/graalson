package au.com.devnull.graalson;

import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public final class GraalsonBoolean extends GraalsonValue {

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
        if (this.value.asBoolean()) {
            return ValueType.TRUE;
        } else {
            return ValueType.FALSE;
        }
    }

    @Override
    public Value getGraalsonValue() {
        return value;
    }

}
