package org.mcnip.solver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import org.mcnip.solver.Contractors.BoundContractor.GreaterEqualsContractor;
import org.mcnip.solver.Contractors.BoundContractor.LessEqualsContractor;
import org.mcnip.solver.Model.*;
import org.mcnip.solver.SatSolver.Solver;

import lombok.NoArgsConstructor;


/**
 * This class should provide the core functionality around our solver.
 * Including managing the data.
 * Manipulating the data
 */
public class Context {
    
    public Context(IParser parser, Map<String, Interval> varIntervals, Solver solver) 
    {
        this.satSolver = solver;
        this.varIntervals = varIntervals; // do not remove this yet! Needed until tests are adapted.
        this.parser = parser;
        
        // init context state:
        this.formula = parser.getFormula();
        intervalAssignmentStack.push(parser.getIntervals());
        // assertedAtoms stays empty in the beginning.
    }
    
    /**
     * Actually varIntervals is not necessary. We use varAssignmentStack instead.
     * Some design choices need to be made.
     * Among them is the question: 
     * How do we manage variables and their intervals?
     * Should we store the Intervals as fields in variables?
     * Or should we store them separately in another hashmap?
     * I would argue to store them separately, because they are not strictly AST elements.
     * Unless they actually are (depending on parser input examples).
     *
     * Omega verschwinden lassen: 4.5
     */
    Map<String, Interval> varIntervals;
    
    /**
     * The satSolver should implement the "Solver" interface
     */
    private Solver satSolver;

    /**
     * Provides the formula in CNF.
     */
    private IParser parser;

    /**
     * Proof state as in 4.2, page 219:
     * Corresponds to {@code M} as in the paper.
     * <p>
     * In the paper we have:
     * <pre>
     * atom     = bound   | equation, 
     * equation = triplet | pair
     * </pre>
     * 
     * <p>
     * 
     * However we need to find insert "markers" as backtracking points
     * into out list of asserted atoms.
     * <p>
     * 
     * Therefore programatically we define:
     * 
     * <pre>
     * atom       = {@link org.mcnip.solver.Model.Constraint} | marker
     * constraint = {@link org.mcnip.solver.Model.Bool} | {@link org.mcnip.solver.Model.Bound} | {@link org.mcnip.solver.Model.Triplet} | {@link org.mcnip.solver.Model.Pair}
     * </pre>
     * 
     * The marker symbol that denotes the backtracking point 
     * is an instance of "Marker.class" (IMPORTANT). 
     * Implementation of Marker class is just below this Context class.
     * <p>
     * 
     * i.e. current element = e 
     * 
     * <pre>
     * if(e instanceof Marker){ doStuff(); }
     * </pre>
     * 
     * This solution is better than Nullpointers.
     * 
     * <p>
     * 
     * For now we use a Deque, as it supports both stack and list operations.
     * Since the data structure "M" (assertedAtoms) is stacklike we can just use this.
     */
    Deque<Atom> assertedAtoms = new ArrayDeque<>();
    
    /**
     * since we are supposed to store all bounds and intervals, we can just store bounds as intervals as well.
     *  
     * <pre>
     * x > 3 => 
     *Interval(x, new IPSNumber(DOUBLE.NEGATIVE_INFINITY, INT), new IPSNumber(3, INT), true, false)
     * </pre>
     * <p>
     * Maybe we can find a better solution for that?
     * However this works and it is a suitable solution!
     */
    Stack<Map<String, Interval>> intervalAssignmentStack = new Stack<>();

    /**
     * Currently active formula. Can be extended by our Sat Solver, 
     * usually conflict clauses will be added by Sat Solver.
     */
    Formula formula;

    /**
     * According to the paper, auxiliary variables introduced by rewriting code into 
     * three-adress form are allowed to have empty or faulty intervals.
     * The reason is: we might compute intervals for these variables, but it is possible we never use them.
     * Therefore they introduce an inverted omega to denote thhis extra provision.
     * <p>
     * We maintain a deque of currently asserted "faulty" variables.
     * <p> Similarly to {@link org.mcnip.solver.Context#assertedAtoms} 
     * we insert a {@link org.mcnip.solver.Marker} into the deque as a backtracking point.
     * Here however, they need to be literal markers i.e. "|".
     */
    Deque<String> omega = new ArrayDeque<>();

    public void assertUnitClauses()
    {
        //Step 2 from the paper
        HelpFunctionsKt.findUnits(formula.getClauses(), intervalAssignmentStack.peek()).forEach(assertedAtoms::push);
    }

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
     * Implements the update_rho function from the paper.
     * Consult the test cases in AppTest.java for more details.
     *
     * @param intervals A Map of variables and their associated Intervals.
     * @param constraint A Constraint (i.e. Bound or Pair or Triple).
     * @return Contracted intervals, find them with their name.
     */
    public static Map<String, Interval> updateIntervals(Map<String, Interval> intervals, Constraint constraint) {
        return constraint.getContractor().contract(intervals, constraint.getVariables());
    }

    /**
     * 
     * @param intervals Map of varNames and corresponding Intervals.
     * @return List of Bound constraints corresponding to input Intervals.
     */
    List<Bound> extractBounds(Map<String, Interval> intervals)
    {
        List<Bound> res = new ArrayList<>();
        for(Interval i : intervals.values())
        {
            res.add(new Bound(i.getVarName(), 
                    new DotInterval("_cons" + i.getLowerBound().toString(), i.getLowerBound()), 
                    new GreaterEqualsContractor()));
            res.add(new Bound(i.getVarName(), 
                    new DotInterval("_cons" + i.getUpperBound().toString(), i.getUpperBound()), 
                    new LessEqualsContractor()));
        }
        return res;
    }

    /*
     * If any single one interval of these is empty, this constraint is not valid to
     * make any changes to our current data structure. Therefore we first check whether
     * there is an invalid result.
     * If there is an invalid result we have to check whether it's a problem variable,
     * or a auxiliary variable.
     * @param in 
     * @return
     */
    boolean checkForEmptyInterval(Map<String, Interval> in)
    {
        return in.values().stream().anyMatch(v -> v.isEmpty());
    }

    /**
     * adds a {@link org.mcnip.solver.Marker} onto the assertedAtoms stack.
     * also adds a marker ("|") onto the omega stack.
     */
    void addMarker()
    {
        assertedAtoms.push(new Marker());
        omega.push("|");
    }

}

@NoArgsConstructor
class Marker implements Atom {}