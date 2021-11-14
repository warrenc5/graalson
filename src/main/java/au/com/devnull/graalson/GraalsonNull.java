package au.com.devnull.graalson;

import javax.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonNull implements GraalsonValue {

    public GraalsonNull(JsonValue NULL) {
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
