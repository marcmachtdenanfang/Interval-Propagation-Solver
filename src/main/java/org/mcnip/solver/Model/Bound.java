package org.mcnip.solver.Model;

import org.mcnip.solver.Contractors.Contractor;

import lombok.Getter;

public class Bound extends Constraint {
    
    @Getter private String varName;
    @Getter private Interval bound;

    /**
     * Bounds for our variables.
     * Booleans are typically just bounded by >= 1 or <= 0 for control flow.
     * Therefore we do not really need to take special care for boolean variables
     * during contraction as we can just avoid calling contraction on boolean bounds.
     * 
     * @param varName Name of the variable.
     * @param bound A rational number, represnted via a DotInterval or Interval.
     * @param contractor The relation (<, <=, =, >, >=).
     */
    public Bound(String varName, Interval bound, Contractor contractor)
    {
        super(contractor);
        this.varName = varName;
        this.bound = bound;
    }

    public String[] getVariables()
    {
        return new String[]{this.varName};
    }

}
