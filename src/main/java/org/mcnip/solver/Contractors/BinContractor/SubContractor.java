package org.mcnip.solver.Contractors.BinContractor;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class SubContractor implements Contractor {
    
    public SubContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.BiContractions.sub(in, names);
    }
    
}
