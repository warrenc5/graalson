package au.com.devnull.graalson;

import javax.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public interface GraalsonValue extends JsonValue {

    Value getGraalsonValue();

}
