package au.com.devnull.graalson;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

public class GraalsonPatch implements JsonPatch {

    private static List<Step> diff(JsonStructure source, JsonStructure target) {
        List<GraalsonPatch.Step> steps = new ArrayList<>();
        return steps;
    }

    JsonArray array = null;
    List<GraalsonPatch.Step> steps;

    static JsonPatch createDiff(JsonStructure source, JsonStructure target) {
        List<GraalsonPatch.Step> steps = diff(source, target);
        return new GraalsonPatch(steps);
    }

    GraalsonPatch(JsonArray array) {
        this();
        this.array = array;
        if (array != null) {
            for (JsonValue val : array) {
                if (val instanceof JsonObject obj) {
                    Operation op = Operation.valueOf(obj.getString("op").toUpperCase());
                    String path = obj.getString("path");
                    JsonValue value = obj.containsKey("value") ? obj.get("value") : null;
                    steps.add(new Step(op, path, value));
                }
            }
        }
    }

    GraalsonPatch() {
        this.array = null;
        this.steps = new ArrayList<>();
    }

    private GraalsonPatch(List<Step> steps) {
        this();
        this.steps = steps;
        this.array = new GraalsonArray(steps.stream().map(Step::toJsonObject).collect(toList()));
    }

    @Override
    public <T extends JsonStructure> T apply(T target) {
        T result = target;
        switch (target.getValueType()) {
            case ARRAY:
            case OBJECT:
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (result instanceof GraalsonStructure structure) {
            GraalsonStructure copy = structure.deepClone();
            for (Step s : steps) {
                var l = s.pointer.locate(copy);
                if (l != null) {
                    l.execute(s);
                }
            }
            return (T) copy;
        } else {
            throw new UnsupportedOperationException("target should be JsonStructure");
        }
    }

    public JsonArray toJsonArray() {
        return new GraalsonArray(steps.stream().map(Step::toJsonObject).collect(toList()));
    }

    static class Step {

        final Operation op;
        final GraalsonPointer pointer;
        JsonValue value = null;

        Step(Operation op, String path, JsonValue value) {
            this.op = op;
            this.pointer = new GraalsonPointer(path);
            this.value = value;
        }

        Step(Operation op, String path) {
            this(op, path, (JsonValue) null);
        }

        Step(Operation op, String path, String from) {
            this(op, path, new GraalsonString(from));
        }

        JsonObject toJsonObject() {
            if (value != null) {
                return Json.createObjectBuilder(Map.<String, Object>of(
                        "op", op.toString().toLowerCase(),
                        "path", pointer.path,
                        "value", value
                )).build();
            } else {
                return Json.createObjectBuilder(Map.<String, Object>of(
                        "op", op.toString().toLowerCase(),
                        "path", pointer.path
                )).build();
            }
        }
    }
}
