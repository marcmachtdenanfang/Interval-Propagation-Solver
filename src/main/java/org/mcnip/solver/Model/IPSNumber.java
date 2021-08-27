package org.mcnip.solver.Model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import static org.mcnip.solver.Model.Type.*;


/**
 * The wrapper type for numerals in our system.
 * Wraps both floating-points as well as arbitrary size integers.
 * We use BigIntegers in order to avoid integer overflows.
 * Symbolic Infinity can be queried with the IPSNumber's fpValue 
 * (DOUBLE.POSITIVE_INFINITY or DOUBLE.NEGATIVE_INFINITY).
 */
public class IPSNumber implements Comparable<IPSNumber> {
    
    private final double     fpValue;
    private final BigInteger intValue;
    private Type type;

    public static final IPSNumber ZERO = new IPSNumber(0.0, REAL);
    public static final IPSNumber ONE = new IPSNumber(1.0, REAL);
    public static final IPSNumber POS_INF = new IPSNumber(Double.POSITIVE_INFINITY, REAL);
    public static final IPSNumber NEG_INF = new IPSNumber(Double.NEGATIVE_INFINITY, REAL);

    public static final IPSNumber ZERO_int = new IPSNumber(0, INT);
    public static final IPSNumber NEG_ONE_int = new IPSNumber(-1, INT);
    public static final IPSNumber ONE_int = new IPSNumber(1, INT);
    public static final IPSNumber TEN_int = new IPSNumber(10, INT);

    @Override
    public String toString()
    {
        if (intValue == null || type == REAL)
            return "" + fpValue;
        return intValue.toString();
    }

    @Override
    public int compareTo(@NotNull IPSNumber number) {
        return (this.type != INT || this.intValue == null || number.getIntValue() == null) ? Double.compare(fpValue, number.fpValue) : intValue.compareTo(number.intValue);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        return this.compareTo((IPSNumber) object) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fpValue, intValue, type);
    }

    public IPSNumber(double value, Type t)
    {
        this.type = t;
        this.fpValue = value;
        if(value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY)
            this.intValue = null;
        else
            this.intValue = new BigInteger(String.valueOf(Math.round(value)));
    }

    public IPSNumber(int intValue, Type t)
    {
        this.type = t;
        this.fpValue = intValue;
        this.intValue = new BigInteger(String.valueOf(intValue));
    }

    public IPSNumber(BigInteger intValue, Type t)
    {
        this.type = t;
        this.intValue = intValue;
        this.fpValue = intValue.doubleValue();
    }

    public IPSNumber(String strValue, Boolean roundUpOrDown)
    {
        this.type = strValue.contains(".") ? REAL : INT;
        if (strValue.equals("Infinity") || strValue.equals("-Infinity") || strValue.equals("NaN")) {
            System.out.println("THIS SHOULD NOT HAPPEN!");
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            Arrays.asList(ste).forEach(System.out::println);
            System.out.println(strValue);
            System.exit(1);
            this.intValue = null;
        }
        else
            this.intValue = new BigInteger(strValue.replaceFirst("\\..*$", ""));
        double value = Double.parseDouble(strValue);
        this.fpValue = strValue.equals(Double.toString(value))? value : roundUpOrDown ? Math.nextUp(value) : Math.nextDown(value);
    }

    public IPSNumber(Number value, Type t) {
        this.type = t;
        if (value instanceof BigInteger) {
            this.intValue = (BigInteger) value;
            this.fpValue = value.doubleValue();
        }
        else {
            if ((double) value == Double.POSITIVE_INFINITY || (double) value == Double.NEGATIVE_INFINITY)
                this.intValue = null;
            else
                this.intValue = new BigInteger(String.valueOf(Math.round((double) value)));
            this.fpValue = (double) value;
        }
    }

    public Double getFpValue() {
        return fpValue;
    }

    public BigInteger getIntValue() {
        return intValue;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public IPSNumber max(IPSNumber b)
    {
        switch (this.type) 
        {
            case INT:
                if (this.intValue == null && b.getIntValue() == null)
                    return new IPSNumber(Double.max(this.fpValue, b.getFpValue()), INT);
                else if (this.fpValue == Double.POSITIVE_INFINITY || b.getFpValue() == Double.POSITIVE_INFINITY)
                    return new IPSNumber(Double.POSITIVE_INFINITY, INT);
                else if (this.fpValue == Double.NEGATIVE_INFINITY)
                    return new IPSNumber(b.getIntValue(), INT);
                else if (b.getFpValue() == Double.NEGATIVE_INFINITY)
                    return new IPSNumber(Objects.requireNonNull(this.intValue), INT);
                else
                    return new IPSNumber(Objects.requireNonNull(this.intValue).max(b.getIntValue()), INT);
            case REAL:
                return new IPSNumber(Double.max(this.fpValue, b.getFpValue()), REAL);
            default:
                return null;
        }
    }

    public boolean gt(IPSNumber b)
    {
        return this.compareTo(b) > 0;
    }

    public boolean lt(IPSNumber b)
    {
        return this.compareTo(b) < 0;
    }

    public boolean equals(IPSNumber b)
    {
        return this.compareTo(b) == 0;
    }

    public IPSNumber min(IPSNumber b)
    {
        switch (this.type) 
        {
            case INT:
                if (this.intValue == null && b.getIntValue() == null)
                    return new IPSNumber(Double.min(this.fpValue, b.getFpValue()), INT);
                else if (this.fpValue == Double.NEGATIVE_INFINITY || b.getFpValue() == Double.NEGATIVE_INFINITY)
                    return new IPSNumber(Double.NEGATIVE_INFINITY, INT);
                else if (this.fpValue == Double.POSITIVE_INFINITY)
                    return new IPSNumber(b.getIntValue(), INT);
                else if (b.getFpValue() == Double.POSITIVE_INFINITY)
                    return new IPSNumber(Objects.requireNonNull(this.intValue), INT);
                else
                    return new IPSNumber(Objects.requireNonNull(this.intValue).min(b.getIntValue()), INT);
            case REAL:
                return new IPSNumber(Double.min(this.fpValue, b.getFpValue()), REAL);
            default:
                return null;
        }
    }

    public IPSNumber add(IPSNumber b)
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue + b.getFpValue(), INT);
                }
                return new IPSNumber(this.intValue.add(b.getIntValue()), INT);
            case REAL:
                return new IPSNumber(this.fpValue + b.getFpValue(), REAL);
            default:
                return null;
        }
    }

    public IPSNumber plus(@NotNull IPSNumber number) {
        return this.add(number);
    }

    public IPSNumber sub(IPSNumber b)
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue - b.getFpValue(), INT);
                }
                return new IPSNumber(this.intValue.subtract(b.getIntValue()), INT);
            case REAL:
                return new IPSNumber(this.fpValue - b.getFpValue(), REAL);
            default:
                return null;
        }
    }

    public IPSNumber minus(@NotNull IPSNumber number) {
        return this.sub(number);
    }

    public IPSNumber unaryMinus() {
        switch (this.type)
        {
            case INT:
                return ZERO_int.sub(this);
            case REAL:
                return ZERO.sub(this);
            default:
                return null;
        }
    }

    public IPSNumber mul(IPSNumber b)
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue * b.getFpValue(), INT);
                }
                return new IPSNumber(this.intValue.multiply(b.getIntValue()), INT);
            case REAL:
                return new IPSNumber(this.fpValue * b.getFpValue(), REAL);
            default:
                return null;
        }
    }

    public IPSNumber div(IPSNumber b)
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue / b.getFpValue(), INT);
                }
                return new IPSNumber(this.intValue.divide(b.getIntValue()), INT);
            case REAL:
                return new IPSNumber(this.fpValue / b.getFpValue(), REAL);
            default:
                return null;
        }
    }

    public IPSNumber nrt(IPSNumber b)
    {
        double fac = (this.fpValue < 0) ? -1.0 : 1.0;
        return (this.intValue != null) ? new IPSNumber(Math.pow(this.fpValue * fac, 1.0 / b.fpValue) * fac, this.type) : this;
    }

    public IPSNumber exp(int constant)
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, INT);
                }
                return new IPSNumber(this.intValue.pow(constant), INT);
            case REAL:
                return new IPSNumber(Math.pow(this.fpValue, constant), REAL);
            default:
                return null;
        }
    }

    public IPSNumber pow(IPSNumber b)
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(b.getFpValue(), INT);
                }
                return new IPSNumber(this.intValue.pow(b.intValue.intValue()), INT);
            case REAL:
                return new IPSNumber(Math.pow(this.fpValue, b.fpValue), REAL);
            default:
                return null;
        }
    }

    public IPSNumber neg()
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(-1*this.fpValue, INT);
                }
                return new IPSNumber(this.intValue.negate(), INT);
            case REAL:
                return new IPSNumber(-1*this.fpValue, REAL);
            default:
                return null;
        }
    }

    public IPSNumber nextUp()
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, INT);
                }
                return new IPSNumber(this.intValue.add(new BigInteger(String.valueOf(1))), INT);
            case REAL:
                return new IPSNumber(Math.nextUp(this.fpValue), REAL);
            default:
                return null;
        }
    }

    public IPSNumber nextDown()
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, INT);
                }
                return new IPSNumber(this.intValue.subtract(new BigInteger(String.valueOf(1))), INT);
            case REAL:
                return new IPSNumber(Math.nextDown(this.fpValue), REAL);
            default:
                return null;
        }
    }

    private IPSNumber floatRound(Boolean roundUpOrDown)
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, INT);
                }
                return this;
            case REAL:
                return new IPSNumber(roundUpOrDown ? Math.nextUp(this.fpValue) : Math.nextDown(this.fpValue), REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Negation.");
    }

    public IPSNumber padUp() {
        return this.floatRound(true);
    }

    public IPSNumber padDown() {
        return this.floatRound(false);
    }

    public Boolean isInfinite() {
        return this.getFpValue().isInfinite();
    }

}