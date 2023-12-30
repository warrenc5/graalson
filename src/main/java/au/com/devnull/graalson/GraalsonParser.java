package au.com.devnull.graalson;

import java.io.Reader;
import java.math.BigDecimal;
import jakarta.json.JsonStructure;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

/**
 *
 * @author wozza
 */
public class GraalsonParser implements JsonParser {

    public GraalsonParser(Reader reader) {
        throw new UnsupportedOperationException("json stream event parsing not supported");
    }

    public GraalsonParser(JsonStructure obj) {
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Event next() {
        return null;
    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public boolean isIntegralNumber() {
        return false;
    }

    @Override
    public int getInt() {
        return -1;
    }

    @Override
    public long getLong() {
        return -1l;
    }

    @Override
    public BigDecimal getBigDecimal() {
        return null;
    }

    @Override
    public JsonLocation getLocation() {
        return null;
    }

    @Override
    public void close() {
    }

}
