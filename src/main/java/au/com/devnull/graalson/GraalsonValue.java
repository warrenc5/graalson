package au.com.devnull.graalson;

import jakarta.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public interface GraalsonValue extends JsonValue {

    Value getGraalsonValue();

}
