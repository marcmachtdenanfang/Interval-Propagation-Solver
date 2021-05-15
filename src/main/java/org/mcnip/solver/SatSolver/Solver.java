package org.mcnip.solver.SatSolver;

import java.util.List;

import org.mcnip.solver.Model.Clause;
import org.mcnip.solver.Model.Constraint;

public interface Solver {
    
    public List<Constraint> solve(List<Clause> clauses);
    
}
