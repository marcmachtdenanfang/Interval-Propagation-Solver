package org.mcnip.solver.Contractors.BoundContractor;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LessContractor implements Contractor {

    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        return org.mcnip.solver.BoundContractions.less(in, names);
    }

}
