package au.com.devnull.graalson;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.ARRAY;
import static jakarta.json.JsonValue.ValueType.OBJECT;
import jakarta.json.JsonWriter;
import java.io.Writer;

/**
 *
 * @author wozza
 */
public class GraalsonWriter implements JsonWriter {

    GraalsonGenerator gen;

    public GraalsonWriter(Writer writer) {
        this.gen = new GraalsonGenerator(writer);
    }

    public void writeArray(JsonArray array) {
        gen.writeStartArray();
        for (int i = 0; i < array.size(); i++) {
            this.write(array.get(i));
        }
        gen.writeEnd();
        gen.flush();
    }

    public void writeObject(JsonObject object) {

        gen.writeStartObject();
        object.entrySet().forEach(e -> {
            gen.write(e.getKey(), e.getValue());
        });
        gen.writeEnd();
        gen.flush();
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

    @Override
    public void write(JsonValue value) {
        gen.add(value);
    }

    public void close() {
        gen.close();
    }

}
