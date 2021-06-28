package org.mcnip.solver.Contractors;

import java.util.HashMap;
import java.util.Map;

import org.mcnip.solver.Model.Bool;
import org.mcnip.solver.Model.Interval;

public class BoolContractor implements Contractor {
    
    public BoolContractor() {}

    public boolean boolContract(Bool original, Bool other)
    {
        // For now we should assume, that we only call this function on booleans with equal polarity.
        return /*original.getName().equals(other.getName()) &&*/ (original.isPolarity() == other.isPolarity());
    }

    /**
     * This is a bit hacky!
     * This function should not be called, is however necessary, due to this being a Contractor!
     */
    public Map<String, Interval> contract(Map<String, Interval> inputIntervals, String[] names) 
    {
        return new HashMap<>();
    }
}
