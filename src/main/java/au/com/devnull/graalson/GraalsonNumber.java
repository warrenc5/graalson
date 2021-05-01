/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.devnull.graalson;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonNumber;
import org.graalvm.polyglot.Value;

/**
 *
 * @author wozza
 */
public class GraalsonNumber implements JsonNumber, GraalsonValue {

    private final BigDecimal ivalue;
    private final Value value;

    public GraalsonNumber(Number o) {
        this(Value.asValue(o));
    }

    public GraalsonNumber(Value o) {
        this.value = o;
        this.ivalue = BigDecimal.valueOf(value.asFloat());
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
