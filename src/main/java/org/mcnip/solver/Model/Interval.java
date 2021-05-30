package org.mcnip.solver.Model;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Interval {

    @Getter         private String    varName;
    @Getter @Setter private IPSNumber lowerBound;
    @Getter @Setter private IPSNumber upperBound;
    @Getter         private boolean   lowerIsClosed;
    @Getter         private boolean   upperIsClosed;


    public Interval(String name, Type t)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(Double.NEGATIVE_INFINITY, t);
        this.upperBound = new IPSNumber(Double.POSITIVE_INFINITY, t);
        this.lowerIsClosed = true;
        this.upperIsClosed = true;
    }

    
    public Interval(
        String name,
        int lowerBound, 
        int upperBound, 
        boolean lowerIsClosed, 
        boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(lowerBound, Type.INT);
        this.upperBound = new IPSNumber(upperBound, Type.INT);
        this.lowerIsClosed = lowerIsClosed;
        this.upperIsClosed = upperIsClosed;
    }

    public Interval(
        String name,
        IPSNumber lowerBound, 
        IPSNumber upperBound, 
        boolean lowerIsClosed, 
        boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lowerIsClosed = lowerIsClosed;
        this.upperIsClosed = upperIsClosed;
    }

    public Interval(
        String name,
        double lowerBound,
        double upperBound,
        boolean lowerIsClosed,
        boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(lowerBound, Type.REAL);
        this.upperBound = new IPSNumber(upperBound, Type.REAL);
        this.lowerIsClosed = lowerIsClosed;
        this.upperIsClosed = upperIsClosed;
    }

    public Interval(
        String name,
        BigInteger lowerBound,
        BigInteger upperBound,
        boolean lowerIsClosed,
        boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(lowerBound, Type.INT);
        this.upperBound = new IPSNumber(upperBound, Type.INT);
        this.lowerIsClosed = lowerIsClosed;
        this.upperIsClosed = upperIsClosed;
    }
    

}
