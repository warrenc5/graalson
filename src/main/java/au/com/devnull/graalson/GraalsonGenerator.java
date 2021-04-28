package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonProvider.getPolyglotContext;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonGenerator implements JsonGenerator, JsonWriter {

    Stack<Value> v = new java.util.Stack<>();

    Value context = null;
    private final Writer writer;

    public GraalsonGenerator(Writer writer) {
        this.writer = writer;
    }

    @Override
    public JsonGenerator writeStartObject() {
        v.push(valueFor(HashMap.class));
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        return add(name, HashMap.class);
    }

    @Override
    public JsonGenerator writeStartArray() {
        v.push(valueFor(ArrayList.class));
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        return add(name, ArrayList.class);
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator write(String name, String value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator write(String name, int value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator write(String name, long value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator write(String name, double value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        return add(name, value);
    }

    @Override
    public JsonGenerator writeNull(String name) {
        return add(name);
    }

    @Override
    public JsonGenerator writeEnd() {
        context = v.pop();
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        return add(value);
    }

    @Override
    public JsonGenerator write(String value) {
        return add(value);
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        return add(value);
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        return add(value);
    }

    @Override
    public JsonGenerator write(int value) {
        return add(value);
    }

    @Override
    public JsonGenerator write(long value) {
        return add(value);
    }

    @Override
    public JsonGenerator write(double value) {
        return add(value);
    }

    @Override
    public JsonGenerator write(boolean value) {
        return add(value);
    }

    @Override
    public JsonGenerator writeNull() {
        return add(Void.class);
    }

    @Override
    public void close() {
        flush();
        v.clear();
    }

    @Override
    public void flush() {
        getPolyglotContext().getBindings("js").putMember("mine", context);
        getPolyglotContext().eval("js", "result = JSON.stringify(mine,2)");
        Value result = getPolyglotContext().getBindings("js").getMember("result");
        try {
            writer.append(result.toString());
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        v.clear();
    }

    private GraalsonGenerator add(String name, Class clazz) {
        Value k = valueFor(clazz);
        v.peek().putMember(name, k);
        v.push(k);
        return this;
    }

    public static Value valueFor(Class<? extends Object> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            //Map<Object, Object> map = getPolyglotContext().eval("js", "{}").as(Map.class);
            //assert ((Map<Object, Object>) getPolyglotContext().eval("js", "[{}]").as(Object.class)).get(0) instanceof Map;
            //FIXME assert fails, map is null
            //return Value.asValue(ProxyObject.fromMap(new HashMap())); //
            return getPolyglotContext().getBindings("js").getMember("Object").execute();
        } else if (List.class.isAssignableFrom(clazz)) {
            List<Object> list = getPolyglotContext().eval("js", "[]").as(List.class);
            return Value.asValue(list);
        }
        throw new IllegalArgumentException(clazz.getCanonicalName());
    }

    private JsonGenerator add(Class<? extends Object> clazz) {
        v.push(valueFor(clazz));
        return this;
    }

    private JsonGenerator add(String name, Object value) {
        if (v.peek().hasMembers()) {
            v.peek().putMember(name, value instanceof GraalsonValue ? ((GraalsonValue) value).getGraalsonValue() : Value.asValue(value));
        } else {
            throw new IllegalArgumentException("current object is not a map " + v.peek());
        }
        return this;
    }

    private JsonGenerator add(Object value) {
        if (v.peek().hasArrayElements()) {
            v.peek().setArrayElement(v.peek().getArraySize(), value.toString());
        } else {
            throw new IllegalArgumentException("current object is not a map " + v.peek());
        }
        return this;
    }

    @Override
    public void writeArray(JsonArray array) {
        this.writeStartArray();
        for (int i = 0; i < array.size(); i++) {
            this.write(array.get(i));
        }
        this.writeEnd();
        this.flush();
    }

    @Override
    public void writeObject(JsonObject object) {

        this.writeStartObject();
        object.entrySet().forEach(e -> {
            this.write(e.getKey(), e.getValue());
        });
        this.writeEnd();
        this.flush();
    }

    @Override
    public void write(JsonStructure value) {
        switch (value.getValueType()) {
            case ARRAY:
                this.writeArray((JsonArray) value);
                break;
            case OBJECT:
                this.writeObject((JsonObject) value);
                break;
        }
    }
}
