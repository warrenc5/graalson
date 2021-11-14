package au.com.devnull.graalson;

import javax.json.JsonStructure;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonStructure implements JsonStructure {

    private final GraalsonValue value;

    public GraalsonStructure(Value value) {
        this.value = GraalsonProvider.toJsonValue(value);
    }

    @Override
    public ValueType getValueType() {
        return value.getValueType();
    }

    public JsonStructure getValue() {
        return (JsonStructure) value;
    }

}
