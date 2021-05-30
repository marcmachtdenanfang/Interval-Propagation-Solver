package org.mcnip.solver.Model;

import org.mcnip.solver.Contractors.Contractor;
import lombok.Getter;

public class Triplet extends Constraint
{
    
    @Getter private Interval result;
    @Getter private Interval leftArg;
    @Getter private Interval rightArg;

    public Triplet(Interval result, Interval leftArg, Interval rightArg, Contractor contractor)
    {
        super(contractor);
        this.result = result;
        this.leftArg = leftArg;
        this.rightArg = rightArg;
    }

    public String[] getVariables()
    {
        return new String[] {
            result.getVarName(), 
            leftArg.getVarName(),
            rightArg.getVarName()
        };
    }

}
