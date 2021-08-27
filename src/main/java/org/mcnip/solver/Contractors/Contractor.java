package org.mcnip.solver.Contractors;

import java.util.Map;

import org.mcnip.solver.Model.Interval;

/**
 * Add the appropriate contractor to our bounds/pairs/triples. 
 * <p>
 * That way we can easily manage the operand type,
 * as well as handle calling the contraction method.
 */
public interface Contractor {
    /**
     *
     * @param inputIntervals input intervals
     * @param names has a fixed order of values:
     * for binary operations: String[0] is the result, 
     * String[1] is the argument left of the operator, 
     * and String[2] is on the right of the operator.
     *
     * @return Map of contracted intervals.
     */
    Map<String, Interval> contract(Map<String, Interval> inputIntervals, String[] names);

}
