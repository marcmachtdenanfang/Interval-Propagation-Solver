package org.mcnip.solver.Contractors.UnContractors;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class SinContractor implements Contractor {
    
    public SinContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.UnContractions.sin(in, names);
    }

    @Override
    public String toString() {
        return "sin";
    }
}
