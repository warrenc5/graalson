package au.com.devnull.graalson;

import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public final class GraalsonNull extends GraalsonValue {

    public GraalsonNull() {
    }

    @Override
    public Value getGraalsonValue() {
        return Value.asValue(null);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NULL;
    }
}
