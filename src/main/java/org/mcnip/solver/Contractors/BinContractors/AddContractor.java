package org.mcnip.solver.Contractors.BinContractors;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class AddContractor implements Contractor {

    public AddContractor() {}

    /**
     * @param in Map containing the Intervals of the variables in names.
     * @param names First value should be the result, second and third should be the adders.
     */
    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.BiContractions.add(in, names);
    }

    @Override
    public String toString() {
        return "add";
    }

}
