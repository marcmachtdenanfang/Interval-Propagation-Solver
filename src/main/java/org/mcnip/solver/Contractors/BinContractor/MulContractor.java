package org.mcnip.solver.Contractors.BinContractor;

import java.util.HashMap;
import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class MulContractor implements Contractor {
    
    public MulContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return new HashMap<>();
    }
    
}