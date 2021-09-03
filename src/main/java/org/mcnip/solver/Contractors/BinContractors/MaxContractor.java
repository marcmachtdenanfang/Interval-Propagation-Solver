package org.mcnip.solver.Contractors.BinContractors;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

import java.util.Map;

public class MaxContractor implements Contractor {

  public MaxContractor() {}

  public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
  {
    return org.mcnip.solver.contractions.BiContractions.max(in, names);
  }

  @Override
    public String toString() {
        return "max";
    }

}
