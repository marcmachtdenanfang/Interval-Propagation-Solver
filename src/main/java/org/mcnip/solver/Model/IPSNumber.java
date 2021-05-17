package org.mcnip.solver.Model;

import lombok.Getter;

public class IPSNumber {
    
    @Getter private final double     fpValue;
    @Getter private final int        intValue;
    @Getter private final NumberType type;

    public IPSNumber(double fpValue)
    {
        this.type = NumberType.REAL;
        this.fpValue = fpValue;
        this.intValue = (int) fpValue;
    }

    public IPSNumber(int intValue)
    {
        this.type = NumberType.INT;
        this.fpValue = (double) intValue;
        this.intValue = intValue;
    }

    /**
     * example call, a an IPSNUmber, b an IPSNumber
     * 
     * c = a.add(b)
     * 
     * @return
     */
    public IPSNumber add(IPSNumber b) throws Exception
    {
        switch (this.type) 
        {
            case INT:
                switch (b.getType()) 
                {
                    case INT:
                        return new IPSNumber(this.intValue + b.getIntValue());                
                    case REAL:
                        return new IPSNumber(this.fpValue + b.getFpValue());
                    default:
                        break;
                }            
            case REAL:
                return new IPSNumber(this.fpValue + b.getFpValue());
            default:
                break;
        }
        throw new Exception("unexpected NumberType enum in Addition.");

    }

    public IPSNumber sub(IPSNumber b) throws Exception
    {
        switch (this.type) 
        {
            case INT:
                switch (b.getType()) 
                {
                    case INT:
                        return new IPSNumber(this.intValue - b.getIntValue());                
                    case REAL:
                        return new IPSNumber(this.fpValue - b.getFpValue());
                    default:
                        break;
                }            
            case REAL:
                return new IPSNumber(this.fpValue - b.getFpValue());
            default:
                break;
        }
        throw new Exception("unexpected NumberType enum in Subtraction.");
    }
    
    public IPSNumber mul(IPSNumber b) throws Exception
    {
        switch (this.type) 
        {
            case INT:
                switch (b.getType()) 
                {
                    case INT:
                        return new IPSNumber(this.intValue * b.getIntValue());                
                    case REAL:
                        return new IPSNumber(this.fpValue * b.getFpValue());
                    default:
                        break;
                }            
            case REAL:
                return new IPSNumber(this.fpValue * b.getFpValue());
            default:
                break;
        }
        throw new Exception("unexpected NumberType enum in Multiplication.");
    }

    public IPSNumber div(IPSNumber b) throws Exception
    {
        switch (this.type) 
        {
            case INT:
                switch (b.getType()) 
                {
                    case INT:
                        return new IPSNumber(this.intValue / b.getIntValue());                
                    case REAL:
                        return new IPSNumber(this.fpValue / b.getFpValue());
                    default:
                        break;
                }            
            case REAL:
                return new IPSNumber(this.fpValue / b.getFpValue());
            default:
                break;
        }
        throw new Exception("unexpected NumberType enum in Multiplication.");

    }


    public IPSNumber sqrt() throws Exception
    {
        switch(this.type) 
        {
            case INT:
                return new IPSNumber((int) Math.sqrt(this.intValue));
            case REAL:
                return new IPSNumber(Math.sqrt(this.fpValue));
            default:
                break;
        }
        throw new Exception("unexpected NumberType enum in Squareroot.");
    }

    public IPSNumber exp(int constant) throws Exception
    {
        switch(this.type)
        {
            case INT:
                return new IPSNumber((int) Math.pow(this.intValue, constant));
            case REAL:
                return new IPSNumber(Math.pow(this.fpValue, constant));
            default:
                break;
        }
        throw new Exception("unexpected NumberType enum in Exponentiation.");
    }

}
