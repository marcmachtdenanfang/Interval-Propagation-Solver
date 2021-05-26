package org.mcnip.solver.SatSolver;

import java.util.List;

import org.mcnip.solver.Model.Clause;
import org.mcnip.solver.Model.Constraint;
import org.mcnip.solver.Model.Formula;

public interface Solver {
    
    public List<Constraint> solve(Formula clauses);
    
}
