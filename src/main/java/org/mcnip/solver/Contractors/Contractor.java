package org.mcnip.solver.Contractors;

import java.util.Map;

import org.mcnip.solver.Model.Interval;


public interface Contractor {
    /**
     *
     * @param inputIntervals
     * @param names has a fixed order of values:
     * for binary operations: String[0] is the result, 
     * String[1] is the argument left of the operator, 
     * and String[2] is on the right of the operator.

     * @return Map of contracted intervals.
     */    
    public Map<String, Interval> contract(Map<String, Interval> inputIntervals, String[] names);

}