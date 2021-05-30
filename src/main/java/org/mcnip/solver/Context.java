package org.mcnip.solver;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

    // Placeholder for actual Parser implementation.
    // private Parser parser = new Parser();
    
    // CDCL should implement the solver interface
    Solver satSolver;
    
    // Proof state as in 4.2, page 219:
    /**
     * In the paper we have:
     *  atom     = bound | equation, 
     *  equation = triplet | pair
     * 
     * However we need to find insert "markers" as backtracking points
     * into out list of asserted atoms.
     * Therefore programatically we define:
     * atom       = constraint | marker
     * constraint = bound | triplet | pair
     * 
     * The marker symbol that denotes the backtracking point 
     * is an instance of "Marker.class" (IMPORTANT).
     * This solution is better than Nullpointers.
     * 
     * For now we use a Deque, as it supports both stack and list operations.
     * Since the data structure "M" (assertedAtoms) is stacklike we can just use this.
     */
    Deque<Atom> assertedAtoms = new ArrayDeque<>();
    
    /**
     * since we are supposed to store all bounds and intervals, we can just store bounds as intervals as well.
     * => x > 3 => Interval(x, new IPSNumber(DOUBLE.NEGATIVE_INFINITY, INT), new IPSNumber(3, INT), true, false)
     * 
     * Maybe we can find a better solution for that?
     * However this works and it is a suitable solution!
     */
    Stack<List<Interval>> intervalAssignmentStack = new Stack<>();

    /**
     * Currently active formula. Can be extended by our Sat Solver, 
     * usually conflict clauses will be added by Sat Solver.
     */
    Formula formula;

    public void update()
    {
        // placeholder, should call an assignment of clauses from cdcl solver
        // use mockito to handle this in testing.
        List<Constraint> selectedConstraints = satSolver.solve(this.formula);
        
        for(Constraint constraint : selectedConstraints)
        {
            // Find the constraint variables' intervals and put them in a Map.
            HashMap<String, Interval> intervals = new HashMap<>();
            for(String id : constraint.getVariables())
            {
                intervals.put(id, this.varIntervals.get(id));
            }

            Map<String, Interval> tempMap = updateIntervals(intervals, constraint);

            // replace original intervals with the contracted intervals.
            // Not actually intended behaviour!
            // Update later for actual control flow using intervalAssignmentStack.
            for(String k : tempMap.keySet())
            {
                this.varIntervals.replace(k, tempMap.get(k));
            }
        }
    }

    /**
     * Implements the update_\rho function from the paper.
     * Consult the test cases in AppTest.java for more details.
     *
     * @param intervals
     * @param constraint
     * @return Contracted intervals, find them with their name.
     */
    Map<String, Interval> updateIntervals(Map<String, Interval> intervals, Constraint constraint) {
        return constraint.getContractor().contract(intervals, constraint.getVariables());
    }

}
