package org.mcnip.solver.Contractors.UnContractor;

import java.util.HashMap;
import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class CosContractor implements Contractor {
    
    public CosContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return new HashMap<>();
    }
}