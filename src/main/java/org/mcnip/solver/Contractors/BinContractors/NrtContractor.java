package org.mcnip.solver.Contractors.BinContractors;

import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class NrtContractor implements Contractor {

  public NrtContractor() {}

  public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
  {
    return org.mcnip.solver.contractions.BinContractions.nrt(in, names);
  }

  @Override
    public String toString() {
        return "nrt";
    }

}
