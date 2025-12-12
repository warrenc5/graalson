package au.com.devnull.graalson;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.ARRAY;
import static jakarta.json.JsonValue.ValueType.NUMBER;
import static jakarta.json.JsonValue.ValueType.OBJECT;
import static jakarta.json.JsonValue.ValueType.STRING;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

public class GraalsonPatch implements JsonPatch {

    JsonArray array = null;
    List<Step> steps;

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
        this.array = toJsonArray();
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

    public String toString() {
        return toJsonArray().toString();
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

    static JsonPatch createDiff(JsonStructure source, JsonStructure target) {
        List<Step> steps = diff(source, target, "/", new ArrayList<Step>());
        return new GraalsonPatch(steps);
    }

    public static List<Step> diff(JsonValue v1, JsonValue v2, String path, List<Step> steps) {

        if (v1 == null && v2 == null) {
        } else if (v1 == null && v2 != null) {
            steps.add(new Step(Operation.ADD, path, v2));
            return steps;
        } else if (v1 != null && v2 == null) {
            steps.add(new Step(Operation.REMOVE, path, v2));
            return steps;
        }

        if (v1.getValueType() != v2.getValueType()){
            steps.add(new Step(Operation.REPLACE, path, v2));
            return steps;
        }

        switch (v1.getValueType()) {
            case OBJECT: {
                JsonObject o1 = (JsonObject) v1;
                JsonObject o2 = (JsonObject) v2;
                for (String k : o1.keySet()) {
                    if (!o2.containsKey(k)) {
                        String currentPath = path.endsWith("/") ? (path + k) : (path + "/" + k);
                        steps.add(new Step(Operation.REMOVE, currentPath));
                    }
                }
                for (String k : o2.keySet()) {
                    String currentPath = path.endsWith("/") ? (path + k) : (path + "/" + k);
                    if (!o1.containsKey(k)) {
                        steps.add(new Step(Operation.ADD, currentPath, o2.get(k)));
                    } else {
                        diff(o1.get(k), o2.get(k), currentPath, steps);
                    }
                }
                break;
            }
            case ARRAY: {
                JsonArray a1 = (JsonArray) v1;
                JsonArray a2 = (JsonArray) v2;
                int minSize = Math.min(a1.size(), a2.size());
                for (int i = 0; i < minSize; i++) {
                    String currentPath = path + "/" + i;
                    diff(a1.get(i), a2.get(i), currentPath, steps);
                }
                if (a1.size() > a2.size()) {
                    for (int i = a2.size(); i < a1.size(); i++) {
                        String currentPath = path + "/" + i;
                        steps.add(new Step(Operation.REMOVE, currentPath));
                    }
                } else if (a2.size() > a1.size()) {
                    for (int i = a1.size(); i < a2.size(); i++) {
                        String currentPath = path + "/" + i;
                        steps.add(new Step(Operation.ADD, currentPath, a2.get(i)));
                    }
                }
                break;
            }
            case STRING: {
                JsonString s1 = (JsonString) v1;
                JsonString s2 = (JsonString) v2;
                if (!s1.getString().equals(s2.getString())) {
                    steps.add(new Step(Operation.REPLACE, path, v2));
                }
                break;
            }
            case NUMBER: {
                JsonNumber n1 = (JsonNumber) v1;
                JsonNumber n2 = (JsonNumber) v2;
                if (n1.bigDecimalValue().compareTo(n2.bigDecimalValue()) != 0) {
                    steps.add(new Step(Operation.REPLACE, path, v2));
                }
                break;
            }
            case TRUE:
            case FALSE: {
                if (v1.getValueType() != v2.getValueType()) {
                    steps.add(new Step(Operation.REPLACE, path, v2));
                }
                break;
            }
            case NULL: {
                if (v2.getValueType() != JsonValue.ValueType.NULL) {
                    steps.add(new Step(Operation.REPLACE, path, v2));
                }
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
        return steps;
    }
}
