package org.mcnip.solver.Contractors.BinContractor;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

import java.util.Map;

public class MinContractor implements Contractor {

  public MinContractor() {}

  public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
  {
    return org.mcnip.solver.contractions.BiContractions.min(in, names);
  }

}
