package org.mcnip.solver.Contractors.BinContractor;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class PowContractor implements Contractor {

    public PowContractor() {}

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.BiContractions.pow(in, names);
    }

    @Override
    public String toString() {
        return "pow";
    }
}
