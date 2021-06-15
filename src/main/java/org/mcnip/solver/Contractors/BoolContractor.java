package org.mcnip.solver.Contractors;

import java.util.HashMap;
import java.util.Map;

import org.mcnip.solver.Model.Bool;
import org.mcnip.solver.Model.Interval;

public class BoolContractor implements Contractor {
    
    public BoolContractor() {}

    public boolean boolContract(Bool original, Bool other)
    {
        return original.getName().equals(other.getName());
    }

    /**
     * This is a bit hacky!
     */
    public Map<String, Interval> contract(Map<String, Interval> inputIntervals, String[] names) 
    {
        return new HashMap<>();
    }
}
