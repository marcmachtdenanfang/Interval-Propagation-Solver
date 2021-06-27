package org.mcnip.solver.Model;

import org.mcnip.solver.Contractors.Contractor;

/**
 * Pairs and Triplets are always equalities. 
 * They are never any other relation.
 */
public class Pair extends Constraint
{
   
    private Interval result;
    private Interval origin;

    /**
     * @param result Value or variable that expression results in.
     * @param origin Expression or variable that constrains the result.
     */
    public Pair(Interval result, Interval origin, Contractor contractor)
    {
        super(contractor);
        this.result = result;
        this.origin = origin;
    }

    public String[] getVariables()
    {
        return new String[] {
            result.getVarName(), 
            origin.getVarName(),
        };
    }

    @Override
    public String toString()
    {
        return result + " = " + this.getContractor().getClass().getSimpleName() + " " + origin;
    }

    public Interval getResult() {
        return result;
    }

    public Interval getOrigin() {
        return origin;
    }
}
