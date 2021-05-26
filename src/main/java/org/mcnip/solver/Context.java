package org.mcnip.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mcnip.solver.Model.*;
import org.mcnip.solver.SatSolver.Solver;


/**
 * This class should provide the core functionality around our solver.
 * Including managing the data.
 * Manipulating the data
 */
public class Context {
    
    public Context(Solver solver, Map<String, Interval> varIntervals) 
    {
        this.satSolver = solver;
        this.varIntervals = varIntervals;
    }

    /**
     * Some design choices need to be made.
     * Among them is the question: 
     * How do we manage variables and their intervals?
     * Should we store the Intervals as fields in variables?
     * Or should we store them separately in another hashmap?
     * I would argue to store them separately, because they are not strictly AST elements.
     * Unless they actually are (depending on parser input examples).
     */
    Map<String, Interval> varIntervals;

    Formula formula;

    // Placeholder for actual Parser implementation.
    // private Parser parser = new Parser();

    // CDCL should implement the solver interface
    Solver satSolver;

    


    /**
     * Implements the update_\rho function from the paper.
     */
    public void update()
    {
        // placeholder, should call an assignment of clauses from cdcl solver
        List<Constraint> selectedConstraints = satSolver.solve(this.formula);
        
        for(Constraint constraint : selectedConstraints)
        {
            // Find the constraints variables and put them in a Map.
            HashMap<String, Interval> intervals = new HashMap<>();
            for(String id : constraint.getVariables())
            {
                intervals.put(id, this.varIntervals.get(id));
            }

            Map<String, Interval> tempMap = updateIntervals(intervals, constraint);

            // replace original intervals with the contracted intervals.
            for(String k : tempMap.keySet())
            {
                this.varIntervals.replace(k, tempMap.get(k));
            }

        }
    }

    Map<String, Interval> updateIntervals(Map<String, Interval> intervals, Constraint constraint) {
        /*
        // Using this approach we can remove the first argument of the method (Map<String, Interval> intervals)
        // Disadvantage: this method is reliant on state.
        // Do we want that? 
        // Not really => return Map of the intervals, thereby THIS method is not stateful.
        // Easier to test among other benefits.
        HashMap<String, Interval> i2 = new HashMap<>();
        for(String id : constraint.getVariables())
        {
            i2.put(id, varIntervals.get(id));
        }

        Map<String, Interval> r = constraint.getContractor().contract(i2, constraint.getVariables());

        for(String id : r.keySet())
        {
            varIntervals.replace(id, intervals.get(id), r.get(id));
        }
        */
        
        Map<String, Interval> r = constraint.getContractor().contract(intervals, constraint.getVariables());

        for(String id : r.keySet())
        {
            intervals.replace(id, r.get(id));
        }

        return r;
    }

}
