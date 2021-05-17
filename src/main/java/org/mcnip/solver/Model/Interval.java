package org.mcnip.solver.Model;

import lombok.Getter;
import lombok.Setter;

public class Interval {

    @Getter         private String  varName;
    @Getter @Setter private int     lowerBound;
    @Getter @Setter private int     upperBound;
    @Getter @Setter private boolean lowerIsClosed;
    @Getter @Setter private boolean upperIsClosed;


    public Interval(String name)
    {
        this.varName = name;
        this.lowerBound = Integer.MIN_VALUE;
        this.upperBound = Integer.MAX_VALUE;
        this.lowerIsClosed = false;
        this.upperIsClosed = false;
    }

    
    public Interval(String name,
                    int lowerBound, 
                    int upperBound, 
                    boolean lowerIsClosed, 
                    boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lowerIsClosed = lowerIsClosed;
        this.upperIsClosed = upperIsClosed;
    }
    

}
