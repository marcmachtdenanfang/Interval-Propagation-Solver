package org.mcnip.solver.Model;

import org.mcnip.solver.Contractors.Contractor;

public class Triplet extends Constraint
{
    
    private Interval result;
    private Interval leftArg;
    private Interval rightArg;

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

    @Override
    public String toString()
    {
        return result + " = " + leftArg + " " + this.getContractor().getClass().getSimpleName() + " " + rightArg;
    }

    public Interval getResult() {
        return result;
    }

    public Interval getLeftArg() {
        return leftArg;
    }

    public Interval getRightArg() {
        return rightArg;
    }
}
