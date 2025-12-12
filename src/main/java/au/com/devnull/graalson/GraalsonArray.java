package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonValue.toJsonValue;
import static au.com.devnull.graalson.GraalsonStructure.valueFor;
import static au.com.devnull.graalson.GraalsonStructure.ARRAY_CLASS;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import static jakarta.json.JsonPatch.Operation.ADD;
import static jakarta.json.JsonPatch.Operation.COPY;
import static jakarta.json.JsonPatch.Operation.MOVE;
import static jakarta.json.JsonPatch.Operation.REMOVE;
import static jakarta.json.JsonPatch.Operation.REPLACE;
import static jakarta.json.JsonPatch.Operation.TEST;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import org.graalvm.polyglot.Value;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.Collections;

/**
 *
 * @author wozza
 */
public final class GraalsonArray extends GraalsonStructure implements JsonArray {

    public GraalsonArray(Set o) {
        this(valueFor(ARRAY_CLASS));
        this.addAll(o);
    }

    public GraalsonArray(List o) {
        this(valueFor(ARRAY_CLASS));
        this.addAll(o);
    }

    public GraalsonArray(Value value) {
        this.value = value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }

    @Override
    public JsonValue get(int index) {
        return toJsonValue(value.getArrayElement(index));
    }

    @Override
    public int size() {
        return (int) value.getArraySize();
    }

    @Override
    public JsonObject getJsonObject(int index) {
        return toJsonValue(value.getArrayElement(index), JsonObject.class);
    }

    @Override
    public JsonArray getJsonArray(int index) {
        return toJsonValue(value.getArrayElement(index), JsonArray.class);
    }

    @Override
    public JsonNumber getJsonNumber(int index) {
        return toJsonValue(value.getArrayElement(index), JsonNumber.class);
    }

    @Override
    public JsonString getJsonString(int index) {
        return toJsonValue(value.getArrayElement(index), JsonString.class);
    }

    public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz must not be null");
        }
        int sz = size();
        if (sz == 0) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++) {
            JsonValue val = get(i);
            if (clazz.isInstance(val)) {
                result.add(clazz.cast(val));
            } else {
                throw new ClassCastException("Element at index " + i + " cannot be cast to " + clazz.getName());
            }
        }
        return result;
    }

    @Override
    public String getString(int index) {
        return value.getArrayElement(index).asString();
    }

    @Override
    public String getString(int index, String defaultValue) {
        return value.getArrayElement(index).asString();
    }

    @Override
    public int getInt(int index) {
        return value.getArrayElement(index).asInt();
    }

    @Override
    public int getInt(int index, int defaultValue) {
        return value.getArrayElement(index).asInt();
    }

    @Override
    public boolean getBoolean(int index) {
        return value.getArrayElement(index).asBoolean();
    }

    @Override
    public boolean getBoolean(int index, boolean defaultValue) {
        return value.getArrayElement(index).asBoolean();
    }

    @Override
    public boolean isNull(int index) {
        return value.getArrayElement(index).isNull();
    }

    @Override
    public Value getGraalsonValue() {
        return this.value;
    }

    public GraalsonArray deepClone() {
        return new GraalsonArray(GraalsonValue.deepClonePolyglotValue(this.value));
    }

    @Override
    public <T extends JsonStructure> T execute(GraalsonPatch.Step step) {
        switch (step.op) {
            case ADD: {
                //JsonValue target = step.pointer.deriveParentPath().getValue(this);
                this.add(step.pointer.lastPathComponentIndex(), step.value);
                break;
            }
            case REMOVE: {
                JsonValue target = step.pointer.getValue(this);
                if (target != null) {
                    target.asJsonArray().remove(step.pointer.lastPathComponentIndex());
                }
                break;
            }
            case REPLACE: {
                JsonValue target = step.pointer.getValue(this);

                if (target != null) {
                    target.asJsonArray().add(step.pointer.lastPathComponentIndex(), step.value);
                }
                break;
            }
            case COPY:
                break;
            case MOVE:
                break;
            case TEST:
                break;
            default:
        }
        return (T) this;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return StreamSupport.stream(
                Spliterators.spliterator(iterator(), size(), 0), false)
                .anyMatch(e -> (o == null && e == null) || (o != null && o.equals(e)));
    }

    @Override
    public Iterator<JsonValue> iterator() {
        return new Iterator<JsonValue>() {
            private final long arraySize = value.getArraySize();
            private long index = 0;

            @Override
            public boolean hasNext() {
                return index < arraySize;
            }

            @Override
            public JsonValue next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return toJsonValue(value.getArrayElement(index++));
            }
        };
    }

    public Object[] toArray() {
        ArrayList<JsonValue> list = new ArrayList<>((int) value.getArraySize());
        for (long i = 0; i < value.getArraySize(); i++) {
            list.add(toJsonValue(value.getArrayElement(i)));
        }
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] newArray = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
            a = newArray;
        }
        int i = 0;
        for (JsonValue val : this) {
            @SuppressWarnings("unchecked")
            T element = (T) val;
            a[i++] = element;
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(JsonValue e) {
        value.setArrayElement(value.getArraySize(), toValue(e));
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends JsonValue> c) {
        boolean modified = false;
        for (JsonValue e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends JsonValue> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        long size = value.getArraySize();
        for (long i = size - 1; i >= 0; i--) {
            value.removeArrayElement(i);
        }
    }

    public JsonValue set(int index, JsonValue element) {
        JsonValue oldValue = toJsonValue(value.getArrayElement(index));
        value.setArrayElement(index, toValue(element));
        return oldValue;
    }

    public void add(int index, JsonValue element) {
        long size = value.getArraySize();
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        for (long i = size - 1; i >= index; i--) {
            value.setArrayElement(i + 1, value.getArrayElement(i));
        }
        value.setArrayElement(index, toValue(element));
    }

    public JsonValue remove(int index) {
        long size = value.getArraySize();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        JsonValue removedValue = toJsonValue(value.getArrayElement(index));
        for (long i = index; i < size - 1; i++) {
            value.setArrayElement(i, value.getArrayElement(i + 1));
        }
        value.removeArrayElement(size - 1);
        return removedValue;
    }

    @Override
    public int indexOf(Object o) {
        Iterator<JsonValue> it = iterator();
        int index = 0;
        while (it.hasNext()) {
            JsonValue value = it.next();
            if (o == null ? value == null : o.equals(value)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<JsonValue> listIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<JsonValue> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JsonValue> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
