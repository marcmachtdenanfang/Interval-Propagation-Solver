package org.mcnip.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mcnip.solver.Model.*;
// import org.mcnip.solver.Model.Clause;
// import org.mcnip.solver.Model.Constraint;
// import org.mcnip.solver.Model.Formula;
// import org.mcnip.solver.Model.Interval;
import org.mcnip.solver.Parser.AST;

/**
 * This class should provide the core functionality around our solver.
 * Including managing the data.
 * Manipulating the data
 */
public class Context {
    
    public Context() {}

    /**
     * Some design choices need to be made.
     * Among them is the question: 
     * How do we manage variables and their intervals?
     * Should we store the Intervals as fields in variables?
     * Or should we store them separately in another hashmap?
     * I would argue to store them separately, because they are not strictly AST elements.
     * Unless they actually are (depending on parser input examples).
     */
    private HashMap<String, Interval> varIntervals = new HashMap<>();

    private Formula formula;

    // Placeholder for actual Parser implementation.
    // private Parser parser = new Parser();

    // CDCL should implement the solver interface
    // private Solver satSolver = new CDCL(clauses);


    /**
     * Implements the update_\rho function from the paper.
     */
    public void update()
    {
        // placeholder, should call an assignment of clauses from cdcl solver
        List<Constraint> selectedConstraints = new ArrayList<>(); // satSolver.solve(clauses);
        for(Constraint constraint : selectedConstraints)
        {
            HashMap<String, Interval> intervals = new HashMap<>();
            for(String id : constraint.getVariables())
            {
                intervals.put(id, varIntervals.get(id));
            }
            
            // c
            updateIntervals(intervals, constraint);
        }
    }

    private void updateIntervals(HashMap<String, Interval> intervals, Constraint clause) {
        for(String id : clause.getVariables())
        {

            // AST exp = clause.getArExp();
            // traverse the arithmetic expression, to see what contractors need to be called.
        }
    }

}
