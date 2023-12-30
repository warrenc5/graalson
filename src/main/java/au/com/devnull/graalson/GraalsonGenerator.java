package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.valueFor;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGenerator;
import static au.com.devnull.graalson.GraalsonProvider.jsonStringify;
import java.util.Stack;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonGenerator implements JsonGenerator {

    protected final Writer writer;
    private Stack<Value> v = new java.util.Stack<>();

    private Value context = null;

    protected GraalsonGenerator(Writer writer) {
        this.writer = writer;
    }

    @Override
    public JsonGenerator writeStartObject() {
        v.push(valueFor(Map.class));
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        add(name, Map.class);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        v.push(valueFor(List.class));
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        add(name, List.class);
        return this;
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, String value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, int value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, long value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, double value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        add(name, value);
        return this;
    }

    @Override
    public JsonGenerator writeNull(String name) {
        add(name);
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        context = v.pop();
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator write(String value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator write(boolean value) {
        add(value);
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        add(Void.class);
        return this;
    }

    @Override
    public JsonGenerator writeKey(String key) {
        addKey(key);
        return this;
    }

    @Override
    public void close() {
        flush();
        v.clear();
    }

    @Override
    public void flush() {
        String result = jsonStringify(context);
        try {
            writer.append(result);
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        v.clear();
    }

    private void addKey(String name) {
        v.peek().putMember(name, null);
    }

    protected void add(String name, Class clazz) {
        Value k = valueFor(clazz);
        v.peek().putMember(name, k);
        v.push(k);
    }

    protected void add(Class<? extends Object> clazz) {
        v.push(valueFor(clazz));
    }

    protected void add(String name, Object value) {
        if (v.peek().hasMembers()) {
            v.peek().putMember(name, value instanceof GraalsonValue ? ((GraalsonValue) value).getGraalsonValue() : Value.asValue(value));
        } else {
            throw new IllegalArgumentException("current object is not a map " + v.peek());
        }
    }

    protected void add(Object value) {
        if (v.peek().hasArrayElements()) {
            v.peek().setArrayElement(v.peek().getArraySize(), value instanceof GraalsonValue ? ((GraalsonValue) value).getGraalsonValue() : Value.asValue(value));
        } else {
            throw new IllegalArgumentException("current object is not a map " + v.peek());
        }
    }


}
