package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonStructure.valueFor;
import static au.com.devnull.graalson.GraalsonValue.jsonStringify;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.graalvm.polyglot.Value;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.ARRAY;
import static jakarta.json.JsonValue.ValueType.OBJECT;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

/**
 *
 * @author wozza
 */
public class GraalsonGenerator implements JsonGenerator {

    final GraalsonWriter gwriter;

    GraalsonGenerator(Writer writer) {
        this.gwriter = new GraalsonWriter(writer);
    }

    public class GraalsonWriter implements JsonWriter {

        private final Writer writer;

        public GraalsonWriter(Writer writer) {
            this.writer = writer;
        }

        @Override
        public void writeArray(JsonArray ja) {
            GraalsonGenerator.this.writeArray(ja);
        }

        @Override
        public void writeObject(JsonObject jo) {
            GraalsonGenerator.this.writeObject(jo);
        }

        @Override
        public void write(JsonStructure js) {
            GraalsonGenerator.this.write(js);
        }

        @Override
        public void write(JsonValue v) {
            GraalsonGenerator.this.write(v);
        }

        @Override
        public void close() {
            GraalsonGenerator.this.close();
        }
    }

    Stack<Value> v = new java.util.Stack<>();

    Value context = null;

    @Override
    public JsonGenerator writeStartObject() {
        v.push(valueFor(Map.class));
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        return add(name, Map.class);
    }

    @Override
    public JsonGenerator writeStartArray() {
        v.push(valueFor(List.class));
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        return add(name, List.class);
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
        String result = jsonStringify(context);
        try {
            gwriter.writer.append(result);
            gwriter.writer.flush();
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
            v.peek().setArrayElement(v.peek().getArraySize(), value instanceof GraalsonValue ? ((GraalsonValue) value).getGraalsonValue() : Value.asValue(value));
        } else {
            throw new IllegalArgumentException("current object is not a map " + v.peek());
        }
        return this;
    }

    public void writeArray(JsonArray array) {
        this.writeStartArray();
        for (int i = 0; i < array.size(); i++) {
            this.write(array.get(i));
        }
        this.writeEnd();
        this.flush();
    }

    public void writeObject(JsonObject object) {

        this.writeStartObject();
        object.entrySet().forEach(e -> {
            this.write(e.getKey(), e.getValue());
        });
        this.writeEnd();
        this.flush();
    }

    public void write(JsonStructure value) {
        if (value instanceof GraalsonStructure) {
            value = ((GraalsonStructure) value).getValue();
        }
        switch (value.getValueType()) {
            case ARRAY:
                this.writeArray((JsonArray) value);
                break;
            case OBJECT:
                this.writeObject((JsonObject) value);
                break;
        }
    }

    @Override //Java EE 8.0
    public JsonGenerator writeKey(String string) {
        //TODO:
        this.write("key:" + string);
        return this;
    }

}
