package org.mcnip.solver.Model;

import java.math.BigInteger;

import lombok.Getter;
import lombok.ToString;

@ToString
public class IPSNumber {
    
    @Getter private final double     fpValue;
    @Getter private final BigInteger intValue;
    @Getter private final NumberType type;

    public static final IPSNumber ZERO_fp = new IPSNumber(0.0, NumberType.REAL);
    public static final IPSNumber ONE_fp = new IPSNumber(1.0, NumberType.REAL);
    public static final IPSNumber TEN_fp = new IPSNumber(10.0, NumberType.REAL);
    
    public static final IPSNumber ZERO_int = new IPSNumber(0, NumberType.INT);
    public static final IPSNumber ONE_int = new IPSNumber(1, NumberType.INT);
    public static final IPSNumber TEN_int = new IPSNumber(10, NumberType.INT);


    public IPSNumber(double value, NumberType t)
    {
        this.type = t; // NumberType.REAL;
        this.fpValue = value;
        if(value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY) {
            this.intValue = null;
        } else {
            this.intValue = new BigInteger(String.valueOf((int) value));
        }
    }

    public IPSNumber(int intValue, NumberType t)
    {
        this.type = t; // NumberType.INT;
        this.fpValue = (double) intValue;
        this.intValue = new BigInteger(String.valueOf(intValue));
    }

    public IPSNumber(BigInteger intValue, NumberType t)
    {
        this.type = t; // NumberType.INT;
        this.intValue = intValue;
        this.fpValue = intValue.doubleValue();
    }




    public IPSNumber max(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                return new IPSNumber(this.intValue.max(b.getIntValue()), NumberType.INT);
            case REAL:
                return new IPSNumber(Double.max(this.fpValue, b.getFpValue()), NumberType.REAL);
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
                return new IPSNumber(this.intValue.min(b.getIntValue()), NumberType.INT);
            case REAL:
                return new IPSNumber(Double.min(this.fpValue, b.getFpValue()), NumberType.REAL);
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
                    return new IPSNumber(this.fpValue + b.getFpValue(), NumberType.INT);
                }
                return new IPSNumber(this.intValue.add(b.getIntValue()), NumberType.INT);
            case REAL:
                return new IPSNumber(this.fpValue + b.getFpValue(), NumberType.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Addition.");

    }

    public IPSNumber sub(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue - b.getFpValue(), NumberType.INT);
                }
                return new IPSNumber(this.intValue.subtract(b.getIntValue()), NumberType.INT);
            case REAL:
                return new IPSNumber(this.fpValue - b.getFpValue(), NumberType.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Subtraction.");
    }
    
    public IPSNumber mul(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                if(this.getIntValue() == null || b.getIntValue() == null) {
                    return new IPSNumber(this.fpValue * b.getFpValue(), NumberType.INT);
                }
                return new IPSNumber(this.intValue.multiply(b.getIntValue()), NumberType.INT);
            case REAL:
                return new IPSNumber(this.fpValue * b.getFpValue(), NumberType.REAL);
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
                    return new IPSNumber(this.fpValue / b.getFpValue(), NumberType.INT);
                }
                return new IPSNumber(this.intValue.divide(b.getIntValue()), NumberType.INT);
            case REAL:
                return new IPSNumber(this.fpValue / b.getFpValue(), NumberType.REAL);
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
                    return new IPSNumber(Math.sqrt(this.fpValue), NumberType.INT);
                }
                return new IPSNumber(this.intValue.sqrt(), NumberType.INT);
            case REAL:
                return new IPSNumber(Math.sqrt(this.fpValue), NumberType.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Squareroot.");
    }

    public IPSNumber exp(int constant) // throws Exception
    {
        switch(this.type)
        {
            case INT:
                if(this.getIntValue() == null) {
                    return new IPSNumber(this.fpValue, NumberType.INT);
                }
                return new IPSNumber(this.intValue.pow(constant), NumberType.INT);
            case REAL:
                return new IPSNumber(Math.pow(this.fpValue, constant), NumberType.REAL);
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
                    return new IPSNumber(-1*this.fpValue, NumberType.INT);
                }
                return new IPSNumber(this.intValue.negate(), NumberType.INT);
            case REAL:
                return new IPSNumber(-1*this.fpValue, NumberType.REAL);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Negation.");
    }

}
