package org.mcnip.solver.Model;

import java.util.ArrayList;
import java.util.List;

import org.mcnip.solver.Contractors.Contractor;

import lombok.Getter;


/**
 * Needs to be reworked, to accomodate variables as result/origin.
 * 
 * Pairs and Triplets are always equalities. 
 * They are never any other relation.
 */
public class Pair<T extends Number, U extends Number> extends Constraint
{
   
    @Getter private T result;
    @Getter private U origin;

    /**
     * @param result Value or variable that expression results in.
     * @param origin Expression or variable that constrains the result.
     */
    public Pair(T result, U origin, Contractor contractor)
    {
        super(contractor);
        this.result = result;
        this.origin = origin;
    }

    public List<String> getVariables()
    {
        return new ArrayList<String>();
    }

    void printSomething()
    {
        System.out.println(this.result.getClass());
        System.out.println(this.origin.getClass());
    }
}
