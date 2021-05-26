package org.mcnip.solver.SatSolver.CDCL;

import java.util.ArrayList;
import java.util.List;

import org.mcnip.solver.Model.Clause;
import org.mcnip.solver.Model.Constraint;
import org.mcnip.solver.Model.Formula;
import org.mcnip.solver.SatSolver.Solver;

public class CDCL implements Solver {
    
    public CDCL() {}

    public List<Constraint> solve(Formula clauses)
    {
        return new ArrayList<Constraint>();
    }

}
