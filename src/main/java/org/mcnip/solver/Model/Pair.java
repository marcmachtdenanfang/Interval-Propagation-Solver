package org.mcnip.solver.Model;

import org.mcnip.solver.Contractors.Contractor;
import lombok.Getter;

/**
 * Pairs and Triplets are always equalities. 
 * They are never any other relation.
 */
public class Pair extends Constraint
{
   
    @Getter private Interval result;
    @Getter private Interval origin;

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

    void printSomething()
    {
        System.out.println(this.result.getClass());
        System.out.println(this.origin.getClass());
    }
}
