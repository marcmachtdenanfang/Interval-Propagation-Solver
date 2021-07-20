package org.mcnip.solver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import kotlin.Pair;
import org.mcnip.solver.Contractors.BoundContractor.GreaterEqualsContractor;
import org.mcnip.solver.Contractors.BoundContractor.LessEqualsContractor;
import org.mcnip.solver.Model.*;
import org.mcnip.solver.SatSolver.Solver;

import lombok.NoArgsConstructor;

import static org.mcnip.solver.HelpFunctionsKt.*;


/**
 * This class should provide the core functionality around our solver.
 * Including managing the data.
 * Manipulating the data
 */
public class Context {
    
    public Context(IParser parser, Map<String, Interval> varIntervals, Solver solver) 
    {
        this.satSolver = solver;
        this.varIntervals = new HashMap<String,Interval>(); // do not remove this yet! Needed until tests are adapted.
        this.parser = parser;
        
        // init context state:
        this.formula = parser.getFormula();
        intervalAssignmentStack.push(parser.getIntervals());
        // assertedAtoms stays empty in the beginning.
        logger = Logger.getLogger("abcd");
    }
    
    public Context(IParser parser, Solver solver) 
    {
        this.satSolver = solver;
        this.parser = parser;
        
        // init context state:
        this.formula = parser.getFormula();
        intervalAssignmentStack.push(parser.getIntervals());
        logger = Logger.getLogger("abcd");
    }

    boolean verbosePrinting;

    public static Logger logger;
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
     * Similarly to {@link org.mcnip.solver.Context#intervalAssignmentStack} 
     * we need an easy way to store the currently assigned {@link org.mcnip.solver.Model.Bool}.
     * <p>
     * Since we opted to make them Constraints rather than intervals, we use this data structure.
     */
    //Stack<Map<String, Bool>> boolAssignmentStack = new Stack<>();

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
     * <p> 
     * Similarly to {@link org.mcnip.solver.Context#assertedAtoms} 
     * we insert a {@link org.mcnip.solver.Model.Marker} into the deque as a backtracking point.
     * Here however, they need to be literal markers i.e. "|".
     */
    //Deque<String> omega = new ArrayDeque<>();

    /**
     * Compute the possible options when one or more variables of the constraint are omega.
     * @param c, a constraint.
     * @return 1 if computation can continue. 
     * 0 if unsatisfiable (result is a problem variable, and one of the arguments is omega).
     * -1 if result is or becomes omega.
     */
    /*int omegaContinue(Constraint c)
    {
        boolean ret = false;
        String[] names = c.getVariables();
        for(String x : names) {
            if(omega.contains(x)) ret = true;
        }
        if(ret == false || c instanceof Bool) return 1;

        if(c instanceof Triplet) {
            if( (omega.contains(names[1]) || omega.contains(names[2])) && !(names[0].charAt(0) == '_') ) {
                return 0;
            }
        } else if(c instanceof org.mcnip.solver.Model.Pair) {
            if( omega.contains(names[1]) && !(names[0].charAt(0) == '_') ) {
                return 0;
            }
        } else if(c instanceof Bound) {
            if(omega.contains(names[1]) && !(names[0].charAt(0) == '_') ) {
                return 0;
            }
        }
        return -1;
    }*/

    /**
     * Step 2 from the paper
     * @return
     */
    public boolean assertUnitClauses()
    {
        // System.out.println("huhu bei assertunitclauses");
        //     intervalAssignmentStack.peek().forEach((k,v) -> System.out.println(v));
        //     System.out.println("abcdefghijklmnopqrstuvwxyz");

        List<Constraint> newAtoms = findUnits(formula.getClauses(), intervalAssignmentStack.peek(), assertedAtoms.stream()
                            .takeWhile(a 
                                        -> true 
                                        //-> !(a instanceof Marker)
                                      )
                            .collect(Collectors.toList()));
        if (newAtoms == null)
            return false;
        newAtoms = newAtoms.stream().filter(atom -> !assertedAtoms.contains(atom)).collect(Collectors.toList());
        newAtoms.forEach(assertedAtoms::push);
        return true;
    }

    /**
     * Step 3 from the paper
     */
    public boolean narrowContractions()
    {
        List<Constraint> newUnits;
        do {
            List<Atom> lastAssertedAtoms = new ArrayList<>();

            Iterator<Atom> itr  = assertedAtoms.descendingIterator();
            while(itr.hasNext()) {
                Atom a = itr.next();
                if(a instanceof Marker) break;
                lastAssertedAtoms.add(a);
            }

            itr = assertedAtoms.iterator();
            while(itr.hasNext()) {
                Atom a = itr.next();
                if(a instanceof Marker) break;
                lastAssertedAtoms.add(a);
            }
            
            // assertedAtoms.stream()
            //         .takeWhile(a 
            //                     -> true 
            //                     //-> !(a instanceof Marker)
            //                   )
            //         .collect(Collectors.toList());
            Pair<Map<String, Interval>, List<Bound>> narrowed = narrowContractors(lastAssertedAtoms, intervalAssignmentStack.peek());
            if (narrowed == null)
                return false;
            intervalAssignmentStack.pop();    
            intervalAssignmentStack.push(narrowed.getFirst());
            
            // System.out.println("huhu");
            // intervalAssignmentStack.peek().forEach((k,v) -> System.out.println(v));
            // System.out.println("abcdefghijklmnopqrstuvwxyz");

            // assertedAtoms.addAll(narrowed.getSecond());
            // lastAssertedAtoms.addAll(narrowed.getSecond());
            // for(Bound b : narrowed.getSecond()) {
            //     assertedAtoms.push(b);
            //     lastAssertedAtoms.add(b);
            // }
            
            newUnits = findUnits(formula.getClauses(), narrowed.getFirst(), lastAssertedAtoms);
            if (newUnits == null)
                return false;
            newUnits = newUnits.stream().filter(unit -> !assertedAtoms.contains(unit)).collect(Collectors.toList());
            newUnits.forEach(assertedAtoms::push);
        } while (!newUnits.isEmpty());
        return true;
    }

    /**
     * Step 4 ideas
     */
    public boolean splitVariableInterval()
    {
        //select unassigned Bool or inconclusive Number, if not possible/too small:
        /*Bool unassignedBool = findUnassignedBool();
        if(unassignedBool != null) {
            assertedAtoms.push(new Marker());
            assertedAtoms.push(unassignedBool);
            return true;
        }*/

        // Necessary Changes:
        // 1. only choose from problem variables, that are actually in our current set of unit clauses.
        // 2. sort those intervals by interval size (ascending).
        Map<String, Interval> vars = intervalAssignmentStack.peek();
        List<String> problemVars = new ArrayList<>();

        // filter vars so we only get problemVars with interval size greater than one
        vars.forEach(
            (k,v) -> { 
                if(/*k.charAt(0) != '_' &&*/ v.containsMoreThanOneValue()) {
                    problemVars.add(k);
                }
            }
        );
        
        if(problemVars.isEmpty()) return false;

        
        Random rand = new Random();
        int counter = rand.nextInt(problemVars.size());
        String variableToSplit = problemVars.get(counter);
        Interval x = vars.get(variableToSplit);
        
        IPSNumber c = x.getMidPoint();
        
        Bound bound = new Bound(x.getVarName(), new DotInterval(c.toString(), c), new LessEqualsContractor());
                
        assertedAtoms.push(new Marker());
        assertedAtoms.push(bound);
        
        Map<String, Interval> toUpdate = Map.of(x.getVarName(), vars.get(x.getVarName()));
        Map<String, Interval> updateV = updateIntervals(toUpdate, bound);
        HashMap<String, Interval> tempMap = new HashMap<>();
        vars.forEach((k,v) -> tempMap.put(k,v));
        tempMap.putAll(updateV);
        intervalAssignmentStack.push(tempMap);

        return true;
    }

    /**
     * Step 5 from the paper
     */
    public boolean revertPreviousSplit()
    {
        if(this.verbosePrinting) {
            System.out.println("backtrack");
        }

        // intervalAssignmentStack.peek().forEach((k,v) -> System.out.println(v));
        // System.out.println("still backtracking");        
        intervalAssignmentStack.pop();

        // intervalAssignmentStack.peek().forEach((k,v) -> System.out.println(v));
        // System.out.println("end backtrack printing");
        Atom guiltyAtom;
        if (assertedAtoms.size() == 0)
            return false;
        Atom marker = assertedAtoms.pop();
        do {
            guiltyAtom = marker;
            if (assertedAtoms.size() == 0)
                return false;
            marker = assertedAtoms.pop();
        } while (!(marker instanceof Marker));
        guiltyAtom = invert(guiltyAtom);
        assertedAtoms.push(guiltyAtom);
        if (!(guiltyAtom instanceof Bool)) {
            Map<String, Interval> updateGuiltyVariable = update(guiltyAtom, intervalAssignmentStack.peek());
            Map<String, Interval> tempMap = intervalAssignmentStack.pop();
            tempMap.putAll(updateGuiltyVariable);
            intervalAssignmentStack.push(tempMap);
        }
        // System.out.println("step5 after all");    
        // intervalAssignmentStack.peek().forEach((k,v) -> System.out.println(v));
        // System.out.println("step5 printing done");
        return true;
    }

    /*
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
    }*/

    /**
     * Implements the update_rho function from the paper.
     * Consult the test cases in AppTest.java for more details.
     *
     * @param intervals A Map of variables and their associated Intervals.
     * @param constraint A Constraint (i.e. Bound or Pair or Triple).
     * @return Contracted intervals, find them with their name.
     */
    public static Map<String, Interval> updateIntervals(Map<String, Interval> intervals, Constraint constraint) {
        // logger.warning("huhuasdhuiasdhais");
        // logger.warning(constraint.toString());
        // if(intervals.isEmpty()) logger.info("yeahyeahyeahyeahyeah");
        // intervals.forEach((k,v) -> logger.severe(v.toString()));
        var t = constraint.getContractor().contract(intervals, constraint.getVariables());
        // t.forEach((k,v) -> logger.warning(v.toString()));
        return t;
    }

    /**
     * 
     * @param intervals Map of varNames and corresponding Intervals.
     * @return List of Bound constraints corresponding to input Intervals.
     */
    public static List<Bound> extractBounds(Map<String, Interval> intervals)
    {
        List<Bound> res = new ArrayList<>();
        for(Interval i : intervals.values())
        {
            res.add(new Bound(i.getVarName(), 
                    new DotInterval(i.getLowerBound().toString(), i.getLowerBound()),
                    new GreaterEqualsContractor()));
            res.add(new Bound(i.getVarName(), 
                    new DotInterval(i.getUpperBound().toString(), i.getUpperBound()),
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

        boolean ret = false;
        for(Interval i : in.values())
        {
            if(i.isEmpty()){
                ret = true;
                break;
            }
        }
        return ret;
    }

    
    /**
     * adds a {@link org.mcnip.solver.Model.Marker} onto the assertedAtoms stack.
     * also adds a marker ("|") onto the omega stack.
     */
    
    /* void addMarker()
    {
        assertedAtoms.push(new Marker());
        omega.push("|");
    }*/

    /**
     * Use this method to backtrack over our assertionStacks and omegas.
     */
    /*
    void backtrack()
    {
        Atom a;
        while(true) {
            a = assertedAtoms.pop();
            if(a instanceof Marker) break;
        }
        String s;
        while(true) {
            s = omega.pop();
            if(s.equals("|")) break;
        }
        return;
    }
    */

    /*
    boolean strongSatisfiability()
    {
        boolean ret = false;
        for(Clause c : this.formula.getClauses())
        {
            // one of Constraints in c is in assertedAtoms
        }
        // if atom is an equation x = yopz or x = op y then x is 
        

        return ret;
    }
    */

}

