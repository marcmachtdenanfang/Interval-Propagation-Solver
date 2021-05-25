package org.mcnip.solver.Model;

import java.math.BigInteger;

import lombok.Getter;

public class IPSNumber {
    
    @Getter private final double     fpValue;
    @Getter private final BigInteger intValue;
    @Getter private final NumberType type;

    public IPSNumber(double fpValue)
    {
        this.type = NumberType.REAL;
        this.fpValue = fpValue;
        this.intValue = new BigInteger(String.valueOf((int) fpValue));
    }

    public IPSNumber(int intValue)
    {
        this.type = NumberType.INT;
        this.fpValue = (double) intValue;
        this.intValue = new BigInteger(String.valueOf(intValue));
    }

    public IPSNumber(BigInteger intValue)
    {
        this.type = NumberType.INT;
        this.intValue = intValue;
        this.fpValue = intValue.doubleValue();
    }

    public IPSNumber max(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                return new IPSNumber(this.intValue.max(b.getIntValue()));
            case REAL:
                return new IPSNumber(Double.max(this.fpValue, b.getFpValue()));
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Max comparison.");
    }

    public IPSNumber min(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            case INT:
                return new IPSNumber(this.intValue.min(b.getIntValue()));
            case REAL:
                return new IPSNumber(Double.min(this.fpValue, b.getFpValue()));
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in min comparison.");
    }

    /**
     * example call, a an IPSNUmber, b an IPSNumber
     * 
     * c = a.add(b),
     * however c type defines NumberType of returned IPSNumber.
     * 
     * => 
     * c = a.add(b, c.getType())
     * 
    * int c
    * c = Int.add(a,b)
    
     * @return
     */
    public IPSNumber add(IPSNumber b) // throws Exception
    {
        switch (this.type) 
        {
            // For now we assume that arithmetic operations always have equal type.
            case INT:
                return new IPSNumber(this.intValue.add(b.getIntValue()));
            case REAL:
                return new IPSNumber(this.fpValue + b.getFpValue());
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
                return new IPSNumber(this.intValue.subtract(b.getIntValue()));
            case REAL:
                return new IPSNumber(this.fpValue - b.getFpValue());
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
                return new IPSNumber(this.intValue.multiply(b.getIntValue()));
            case REAL:
                return new IPSNumber(this.fpValue * b.getFpValue());
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
                return new IPSNumber(this.intValue.divide(b.getIntValue()));
            case REAL:
                return new IPSNumber(this.fpValue / b.getFpValue());
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
                return new IPSNumber(this.intValue.sqrt());
            case REAL:
                return new IPSNumber(Math.sqrt(this.fpValue));
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
                return new IPSNumber(this.intValue.pow(constant));
            case REAL:
                return new IPSNumber(Math.pow(this.fpValue, constant));
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
                return new IPSNumber(this.intValue.negate());
            case REAL:
                return new IPSNumber(-1*this.fpValue);
            default:
                return null; // break;
        }
        // throw new Exception("unexpected NumberType enum in Negation.");
    }

}
