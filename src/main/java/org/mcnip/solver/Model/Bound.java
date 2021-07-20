package org.mcnip.solver.Model;

import org.mcnip.solver.Contractors.Contractor;

public class Bound extends Constraint {

    private String varName;
    private Interval bound;

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
        return new String[]{ this.varName, this.bound.getVarName() };
    }

    @Override
    public String toString()
    {
        return "Bound: " + varName + " "+ this.getContractor() + " " + bound;
    }

    public String getVarName() {
        return varName;
    }

    public Interval getBound() {
        return bound;
    }

    public boolean isInfinite() {
        return this.bound.isDotInfinite();
    }
}
