package au.com.devnull.graalson;

import jakarta.json.JsonArray;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatch.Operation;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonValue;

class GraalsonPatchBuilder implements JsonPatchBuilder {


    private GraalsonPatch patch;

    GraalsonPatchBuilder() {
        patch = new GraalsonPatch();
    }

    GraalsonPatchBuilder(JsonArray array) {
        patch = new GraalsonPatch(array);
    }

    @Override
    public JsonPatchBuilder add(String path, JsonValue value) {
        return append(new GraalsonPatch.Step(Operation.ADD, path, GraalsonValue.from(value)));
    }

    @Override
    public JsonPatchBuilder add(String path, String value) {
        return append(new GraalsonPatch.Step(Operation.ADD, path, new GraalsonString(value)));
    }

    @Override
    public JsonPatchBuilder add(String path, int value) {
        return append(new GraalsonPatch.Step(Operation.ADD, path, new GraalsonNumber(value)));
    }

    @Override
    public JsonPatchBuilder add(String path, boolean value) {
        return append(new GraalsonPatch.Step(Operation.ADD, path, new GraalsonBoolean(value)));
    }

    @Override
    public JsonPatchBuilder remove(String path) {
        return append(new GraalsonPatch.Step(Operation.REMOVE, path));
    }

    @Override
    public JsonPatchBuilder replace(String path, JsonValue value) {
        return append(new GraalsonPatch.Step(Operation.ADD, path, GraalsonValue.from(value)));
    }

    @Override
    public JsonPatchBuilder replace(String path, String value) {
        return append(new GraalsonPatch.Step(Operation.REPLACE, path, new GraalsonString(value)));
    }

    @Override
    public JsonPatchBuilder replace(String path, int value) {
        return append(new GraalsonPatch.Step(Operation.REPLACE, path, new GraalsonNumber(value)));
    }

    @Override
    public JsonPatchBuilder replace(String path, boolean value) {
        return append(new GraalsonPatch.Step(Operation.REPLACE, path, new GraalsonBoolean(value)));
    }

    @Override
    public JsonPatchBuilder move(String path, String from) {
        return append(new GraalsonPatch.Step(Operation.MOVE, path, from));
    }

    @Override
    public JsonPatchBuilder copy(String path, String from) {
        return append(new GraalsonPatch.Step(Operation.COPY, path, from));
    }

    @Override
    public JsonPatchBuilder test(String path, JsonValue value) {
        return append(new GraalsonPatch.Step(Operation.TEST, path, value));
    }

    @Override
    public JsonPatchBuilder test(String path, String value) {
        return append(new GraalsonPatch.Step(Operation.TEST, path, new GraalsonString(value)));
    }

    @Override
    public JsonPatchBuilder test(String path, int value) {
        return append(new GraalsonPatch.Step(Operation.TEST, path, new GraalsonNumber(value)));
    }

    @Override
    public JsonPatchBuilder test(String path, boolean value) {
        return append(new GraalsonPatch.Step(Operation.TEST, path, new GraalsonBoolean(value)));
    }

    @Override
    public JsonPatch build() {
        return patch;
    }

    private JsonPatchBuilder append(GraalsonPatch.Step patchOperation) {
        patch.steps.add(patchOperation);
        return this;
    }
}
