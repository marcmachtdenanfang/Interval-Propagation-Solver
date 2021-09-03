package org.mcnip.solver.Contractors.UnContractors;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class AbsContractor implements Contractor {
    
    public AbsContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.UnContractions.abs(in, names);
    }

    @Override
    public String toString() {
        return "abs";
    }
}
