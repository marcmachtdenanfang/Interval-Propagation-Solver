package org.mcnip.solver.Model;

import java.math.BigInteger;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;


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

    public static final IPSNumber ZERO = new IPSNumber(0.0, Type.REAL);
    public static final IPSNumber ONE = new IPSNumber(1.0, Type.REAL);
    public static final IPSNumber TEN = new IPSNumber(10.0, Type.REAL);
    public static final IPSNumber POS_INF = new IPSNumber(Double.POSITIVE_INFINITY, Type.REAL);
    public static final IPSNumber NEG_INF = new IPSNumber(Double.NEGATIVE_INFINITY, Type.REAL);

    public static final IPSNumber ZERO_int = new IPSNumber(0, Type.INT);
    public static final IPSNumber ONE_int = new IPSNumber(1, Type.INT);
    public static final IPSNumber TEN_int = new IPSNumber(10, Type.INT);

    @Override
    public String toString()
    {
        if(intValue == null || type == Type.REAL){
            return "" + fpValue;
        }
        return intValue.toString();
    }

    @Override
    public int compareTo(@NotNull IPSNumber number) {
        return (this.type != Type.INT || this.intValue == null || number.getIntValue() == null) ? Double.compare(fpValue, number.fpValue) : intValue.compareTo(number.intValue);
    }

    public IPSNumber(double value, Type t)
    {
        this.type = Type.INT;
        this.fpValue = value;
        if(value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY) {
            this.intValue = null;
        } else {
            this.intValue = new BigInteger(String.valueOf((int) value));
        }
    }

    public IPSNumber(int intValue, Type t)
    {
        this.type = t;
        this.fpValue = (double) intValue;
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
        this.type = strValue.contains(".") ? Type.REAL : Type.INT;
        this.intValue = new BigInteger(strValue.replaceFirst("\\..*$",""));
        this.fpValue = roundUpOrDown ? Math.nextUp(Double.parseDouble(strValue)) : Math.nextDown(Double.parseDouble(strValue));
    }

    public IPSNumber(Number value, Type t) {
        this.type = t;
        if (value instanceof BigInteger) {
            this.intValue = (BigInteger) value;
            this.fpValue = value.doubleValue();
        }
        else {
            this.intValue = new BigInteger(String.valueOf((int) value));
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

    public IPSNumber max(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                if(this.intValue == null || b.getIntValue() == null) {
                    return new IPSNumber(Double.max(this.fpValue, b.getFpValue()), Type.INT);
                }
                return new IPSNumber(this.intValue.max(b.getIntValue()), Type.INT);
            case REAL:
                return new IPSNumber(Double.max(this.fpValue, b.getFpValue()), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Max comparison.");
    }

    /**
     * This is greater than b.
     * @param b
     * @return
     */
    public boolean gt(IPSNumber b)
    {
        switch (this.type)
        {
            case INT:
                if(this.intValue == null || b.getIntValue() == null)
                    return this.fpValue > b.getFpValue();
                return this.intValue.compareTo(b.getIntValue()) == 1;
            case REAL:
                return this.fpValue > b.getFpValue();
        }
        return false;
    }

    /**
     * this is less than b method.
     * @param b
     * @return
     */
    public boolean lt(IPSNumber b)
    {
        switch (this.type)
        {
            case INT:
                if(this.intValue == null || b.getIntValue() == null)
                    return this.fpValue < b.getFpValue();
                return this.intValue.compareTo(b.getIntValue()) == -1;
            case REAL:
                return this.fpValue < b.getFpValue();
        }
        return false;
    }

    /**
     * This is greater than or equal b.
     * @param b
     * @return
     */
    public boolean ge(IPSNumber b)
    {
        switch (this.type)
        {
            case INT:
                if(this.intValue == null || b.getIntValue() == null)
                    return this.fpValue >= b.getFpValue();
                return (this.intValue.compareTo(b.getIntValue()) == 1) || (this.intValue.compareTo(b.getIntValue()) == 0);
            case REAL:
                return this.fpValue >= b.getFpValue();
        }
        return false;
    }

    /**
     * this is less than or equal b method.
     * @param b
     * @return
     */
    public boolean le(IPSNumber b)
    {
        switch (this.type)
        {
            case INT:
                if(this.intValue == null || b.getIntValue() == null)
                    return this.fpValue <= b.getFpValue();
                return this.intValue.compareTo(b.getIntValue()) == -1 || this.intValue.compareTo(b.getIntValue()) == 0;
            case REAL:
                return this.fpValue <= b.getFpValue();
        }
        return false;
    }

    public boolean equals(IPSNumber b)
    {
        switch (this.type)
        {
            case INT:
                if(this.intValue == null || b.getIntValue() == null)
                    return this.fpValue == b.getFpValue();
                return this.intValue.compareTo(b.getIntValue()) == 0;
            case REAL:
                return this.fpValue == b.getFpValue();
        }
        return false;
    }




    public IPSNumber min(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                if(this.intValue == null || b.getIntValue() == null) {
                    return new IPSNumber(Double.min(this.fpValue, b.getFpValue()), Type.INT);
                }
                return new IPSNumber(this.intValue.min(b.getIntValue()), Type.INT);
            case REAL:
                return new IPSNumber(Double.min(this.fpValue, b.getFpValue()), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in min comparison.");
    }

    /**
     * example call, a an IPSNUmber, b an IPSNumber
     * 
     * c = a.add(b),
     * 
     * @return
     */
    public IPSNumber add(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            // For now we assume that arithmetic operations always have equal type.
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue + b.getFpValue(), Type.INT);
                }
                return new IPSNumber(this.intValue.add(b.getIntValue()), Type.INT);
            case REAL:
                return new IPSNumber(this.fpValue + b.getFpValue(), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Addition.");

    }

    public IPSNumber plus(@NotNull IPSNumber number) {
        return this.add(number);
    }

    public IPSNumber sub(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue - b.getFpValue(), Type.INT);
                }
                return new IPSNumber(this.intValue.subtract(b.getIntValue()), Type.INT);
            case REAL:
                return new IPSNumber(this.fpValue - b.getFpValue(), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Subtraction.");
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
                return null; // break;
        }
    }

    public IPSNumber mul(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue * b.getFpValue(), Type.INT);
                }
                return new IPSNumber(this.intValue.multiply(b.getIntValue()), Type.INT);
            case REAL:
                return new IPSNumber(this.fpValue * b.getFpValue(), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Multiplication.");
    }

    public IPSNumber div(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                // This must certainly be changed in the future.
                // infinity/infinity == NaN!
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue / b.getFpValue(), Type.INT);
                }
                return new IPSNumber(this.intValue.divide(b.getIntValue()), Type.INT);
            case REAL:
                return new IPSNumber(this.fpValue / b.getFpValue(), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Multiplication.");

    }


    public IPSNumber sqrt() // throws Exception
    {
        switch(this.type) 
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(Math.sqrt(this.fpValue), Type.INT);
                }
                return new IPSNumber(this.intValue.sqrt(), Type.INT);
            case REAL:
                return new IPSNumber(Math.sqrt(this.fpValue), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Squareroot.");
    }

    public IPSNumber nrt(IPSNumber b) // throws Exception
    {
        return new IPSNumber(Math.pow(this.fpValue, 1.0 / b.fpValue), this.type);
    }

    public IPSNumber exp(int constant) // throws Exception
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, Type.INT);
                }
                return new IPSNumber(this.intValue.pow(constant), Type.INT);
            case REAL:
                return new IPSNumber(Math.pow(this.fpValue, constant), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Exponentiation.");
    }

    public IPSNumber pow(IPSNumber b) // throws Exception
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(b.getFpValue(), Type.INT);
                }
                return new IPSNumber(this.intValue.pow(b.intValue.intValue()), Type.INT);
            case REAL:
                return new IPSNumber(Math.pow(this.fpValue, b.fpValue), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Exponentiation.");
    }

    public IPSNumber neg() // throws Exception
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(-1*this.fpValue, Type.INT);
                }
                return new IPSNumber(this.intValue.negate(), Type.INT);
            case REAL:
                return new IPSNumber(-1*this.fpValue, Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Negation.");
    }

    public IPSNumber inc() // throws Exception
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, Type.INT);
                }
                return new IPSNumber(this.intValue.add(new BigInteger(String.valueOf(1))), Type.INT);
            case REAL:
                return new IPSNumber(Math.nextUp(this.fpValue), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Negation.");
    }

    public IPSNumber dec() // throws Exception
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, Type.INT);
                }
                return new IPSNumber(this.intValue.subtract(new BigInteger(String.valueOf(1))), Type.INT);
            case REAL:
                return new IPSNumber(Math.nextDown(this.fpValue), Type.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Negation.");
    }

    private IPSNumber floatRound(Boolean roundUpOrDown)
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, Type.INT);
                }
                return new IPSNumber(this.intValue, Type.INT);
            case REAL:
                return new IPSNumber(roundUpOrDown ? Math.nextUp(this.fpValue) : Math.nextDown(this.fpValue), Type.REAL);
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

    public Boolean isZeroOrInfinite() {
        return this.fpValue == (double) 0 || this.getFpValue().isInfinite();
    }

}
