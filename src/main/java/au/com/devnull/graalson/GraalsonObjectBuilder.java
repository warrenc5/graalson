/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.devnull.graalson;

import static au.com.devnull.graalson.GraalsonGenerator.valueFor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonObjectBuilder implements JsonObjectBuilder {

    Value value = null;

    public GraalsonObjectBuilder() {
        this.value = valueFor(Map.class);
    }

    @Override
    public JsonObjectBuilder add(String name, JsonValue value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, String value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigInteger value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigDecimal value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, int value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, long value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, double value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, boolean value) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder addNull(String name) {
        this.value.putMember(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        this.value.putMember(name, builder.build());
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        this.value.putMember(name, builder.build());
        return this;
    }

    @Override
    public JsonObject build() {
        return new GraalsonObject(value);
    }

}
