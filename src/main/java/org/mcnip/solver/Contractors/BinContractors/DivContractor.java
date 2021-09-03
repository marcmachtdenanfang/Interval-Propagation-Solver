package org.mcnip.solver.Contractors.BinContractors;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class DivContractor implements Contractor {
    
    public DivContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.BiContractions.div(in, names);
    }

    @Override
    public String toString() {
        return "div";
    }
    
}
