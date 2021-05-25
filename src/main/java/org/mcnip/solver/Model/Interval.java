package org.mcnip.solver.Model;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

public class Interval {

    @Getter         private String    varName;
    @Getter @Setter private IPSNumber lowerBound;
    @Getter @Setter private IPSNumber upperBound;
    @Getter @Setter private boolean   lowerIsClosed;
    @Getter @Setter private boolean   upperIsClosed;


    public Interval(String name)
    {
        this.varName = name;
        this.lowerBound = null;
        this.upperBound = null;
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
        this.lowerBound = new IPSNumber(lowerBound);
        this.upperBound = new IPSNumber(upperBound);
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
        this.lowerBound = new IPSNumber(lowerBound);
        this.upperBound = new IPSNumber(upperBound);
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
        this.lowerBound = new IPSNumber(lowerBound);
        this.upperBound = new IPSNumber(upperBound);
        this.lowerIsClosed = lowerIsClosed;
        this.upperIsClosed = upperIsClosed;
    }
    

}
