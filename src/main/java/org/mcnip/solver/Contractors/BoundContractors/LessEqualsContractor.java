package org.mcnip.solver.Contractors.BoundContractors;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LessEqualsContractor implements Contractor {

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.contractions.BoundContractions.lessEquals(in, names);
    }

    @Override
    public String toString() {
        return "<=";
    }

}
