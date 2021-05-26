package org.mcnip.solver.Model;

import java.util.ArrayList;
import java.util.List;

import org.mcnip.solver.Contractors.Contractor;

import lombok.Getter;

/*
public class Triplet <T extends Number, U extends Number, V extends Number> extends Constraint
{
    
    @Getter private T result;
    @Getter private U leftArg;
    @Getter private V rightArg;

    public Triplet(T result, U leftArg, V rightArg, Contractor contractor)
    {
        super(contractor);
        this.result = result;
        this.leftArg = leftArg;
        this.rightArg = rightArg;
    }

    public List<String> getVariables()
    {
        return new ArrayList<String>();
    }

} 
*/

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
