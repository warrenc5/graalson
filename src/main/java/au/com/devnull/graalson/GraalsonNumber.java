/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.devnull.graalson;

import jakarta.json.JsonNumber;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public final class GraalsonNumber extends GraalsonValue implements JsonNumber {

    static void from(JsonNumber v) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private BigDecimal ivalue;

    GraalsonNumber(int i) {
        this(Value.asValue(i));
    }

    GraalsonNumber(Number o) {
        this(Value.asValue(o));
    }

    GraalsonNumber(Value o) {
        this.value = o;

        if (!o.isNumber()) {
            throw new IllegalArgumentException(o.asString());
        }

        try {
            this.ivalue = BigDecimal.valueOf(value.asFloat());
        } catch (NumberFormatException x) {
            this.ivalue = BigDecimal.valueOf(-0f);
            //if (value.is() && value.asString().equalsIgnoreCase("nan")) {
        }
    }

    @Override
    public boolean isIntegral() {
        return ivalue.scale() == 0;
    }

    @Override
    public BigInteger bigIntegerValue() {
        return ivalue.toBigInteger();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return ivalue.toBigIntegerExact();
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return ivalue;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }

    @Override
    public int intValue() {
        return ivalue.intValue();
    }

    @Override
    public int intValueExact() {
        return ivalue.intValueExact();
    }

    @Override
    public long longValue() {
        return ivalue.longValue();
    }

    @Override
    public long longValueExact() {
        return ivalue.longValueExact();
    }

    @Override
    public double doubleValue() {
        return ivalue.doubleValue();
    }

    @Override
    public Value getGraalsonValue() {
        return value;
    }

    @Override
    public String toString() {
        return ivalue.toPlainString();
    }

}
