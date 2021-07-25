package org.mcnip.solver.Contractors.UnContractor;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class ExpContractor implements Contractor {

    public ExpContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.UnContractions.exp(in, names);
    }

    @Override
    public String toString() {
        return "e^";
    }
    
}
