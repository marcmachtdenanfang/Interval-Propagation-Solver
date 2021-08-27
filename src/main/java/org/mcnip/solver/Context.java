package org.mcnip.solver;

import kotlin.Pair;
import org.mcnip.solver.Contractors.BoundContractor.GreaterEqualsContractor;
import org.mcnip.solver.Contractors.BoundContractor.LessEqualsContractor;
import org.mcnip.solver.Model.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.mcnip.solver.App.ANSI_GREEN;
import static org.mcnip.solver.App.ANSI_RESET;
import static org.mcnip.solver.HelpFunctionsKt.*;


/**
 * This class should provide the core functionality around our solver.
 * Including managing and manipulating the data.
 */
public class Context {
    
    public Context(Formula formula, Map<String, Interval> varIntervals)
    {
        this.formula = formula;
        intervalAssignmentStack.push(varIntervals);
    }

    boolean verbosePrinting;

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
     * Therefore programmatically we define:
     * 
     * <pre>
     * atom       = {@link org.mcnip.solver.Model.Constraint} | marker
     * constraint = {@link org.mcnip.solver.Model.Bound} | {@link org.mcnip.solver.Model.Triplet} | {@link org.mcnip.solver.Model.Pair}
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
     * This solution is better than null pointers.
     * 
     * <p>
     * 
     * For now we use a Deque, as it supports both stack and list operations.
     * Since the data structure "M" (assertedAtoms) is stack-like we can just use this.
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
     * Number of backtracks.
     */
    int backtracks;

    /**
     * Probability of adding auxiliary variables into the pool of variables eligible for splitting.
     */
    int probability = 5;

    /**
     * Number of bits used for splitting integer variables. Necessary due to using unbounded BigIntegers.
     */
    int intPrecision = 128;

    /**
     * Name of variable to minimize.
     */
    String minimize;

    private void printUnits() {
        if (verbosePrinting) {
            System.out.println(ANSI_GREEN + "--- asserted unit clauses" + ANSI_RESET);
            assertedAtoms.forEach(System.out::println);
        }
    }

    private void printContractions() {
        if (verbosePrinting) {
            System.out.println(ANSI_GREEN + "--- narrowed assignments" + ANSI_RESET);
            intervalAssignmentStack.peek().values().forEach(System.out::println);
            System.out.println(ANSI_GREEN + "--- asserted atoms" + ANSI_RESET);
            assertedAtoms.forEach(System.out::println);
        }
    }

    public boolean solve() {
        while (true) {
            if (assertUnitClauses()) {
                printUnits();
                if (narrowContractions()) {
                    printContractions();
                    if (splitVariableInterval())
                        return true;
                }
                else if (revertPreviousSplit())
                    return false;
            }
            else if (revertPreviousSplit())
                return false;
        }
    }

    /**
     * Step 2 from the paper.
     * @return Satisfiability of formula under newly assertedAtoms.
     */
    public boolean assertUnitClauses()
    {
        List<Constraint> newAtoms = findUnits(formula.getClauses(), intervalAssignmentStack.peek());
        if (newAtoms == null)
            return false;
        newAtoms.stream().filter(atom -> !assertedAtoms.contains(atom)).collect(Collectors.toList()).forEach(assertedAtoms::push);
        return true;
    }

    /**
     * Step 3 from the paper with loop back to step 2 as long as it produces new unit clauses.
     * @return Satisfiability of formula under narrowed intervals and additionally assertedAtoms.
     */
    public boolean narrowContractions()
    {
        List<Constraint> newUnits;
        do {
            
            Set<Atom> lastAssertedAtomsSet = new HashSet<>();

            Iterator<Atom> itr  = assertedAtoms.descendingIterator();
            while(itr.hasNext()) {
                Atom a = itr.next();
                if(a instanceof Marker) break;
                lastAssertedAtomsSet.add(a);
            }

            List<Atom> lastAssertedAtoms = new ArrayList<>();
            lastAssertedAtomsSet.forEach(e -> {
                    if(e instanceof Bound) lastAssertedAtoms.add(e);
                    lastAssertedAtoms.add(0, e);
            } );

            Pair<Map<String, Interval>, List<Bound>> narrowed = narrowContractors(lastAssertedAtoms, intervalAssignmentStack.peek());
            if (narrowed == null)
                return false;

            var tempMap = intervalAssignmentStack.pop();
            tempMap.putAll(narrowed.getFirst());
            intervalAssignmentStack.push(tempMap);

            newUnits = findUnits(formula.getClauses(), narrowed.getFirst());
            if (newUnits == null)
                return false;
            newUnits = newUnits.stream().filter(unit -> !assertedAtoms.contains(unit)).collect(Collectors.toList());
            newUnits.forEach(assertedAtoms::push);
        } while (!newUnits.isEmpty());
        return true;
    }

    private int getRandomInt(int size)
    {
        Random rand = new Random();
        return rand.nextInt(size);
    }

    /**
     * Step 4 from the paper.
     * @return True if no more variable can be split. False otherwise.
     */
    public boolean splitVariableInterval()
    {
        Map<String, Interval> vars = intervalAssignmentStack.peek();
        List<String> problemVars = new ArrayList<>();

        // filter vars so we only get problemVars with interval size greater than one
        vars.forEach(
            (k,v) -> {
                if (k.charAt(0) != '_' && v.containsMoreThanOneValue())
                    problemVars.add(k);
            }
        );

        // in 1/probability of cases we add aux variables to our pool of splitting variables.
        if (problemVars.isEmpty() || getRandomInt(this.probability) < 1)
            vars.forEach(
                (k,v) -> {
                    if (k.charAt(0) == '_' && v.containsMoreThanOneValue())
                        problemVars.add(k);
                }
            );
        if (problemVars.isEmpty()) return true;

        int counter = getRandomInt(problemVars.size());
        int preferredVar = (minimize == null) ? -1 : problemVars.indexOf(minimize);
        String variableToSplit = problemVars.get((preferredVar >= 0) ? preferredVar : counter);
        Interval x = vars.get(variableToSplit);
        
        IPSNumber c = x.getMidPoint(intPrecision);
        
        Bound bound = new Bound(x.getVarName(), new DotInterval(c.toString(), c), new LessEqualsContractor());
        assertedAtoms.push(new Marker());
        assertedAtoms.push(bound);

        HashMap<String, Interval> tempMap = new HashMap<>();
        tempMap.putAll(vars);
        tempMap.putAll(updateIntervals(Map.of(x.getVarName(), vars.get(x.getVarName())), bound));
        intervalAssignmentStack.push(tempMap);

        backtracks += 1;
        return false;
    }

    /**
     * Step 5 from the paper.
     * @return True if backtracking is impossible. False otherwise.
     */
    public boolean revertPreviousSplit()
    {
        if (this.verbosePrinting)
            System.out.println("backtrack");

        intervalAssignmentStack.pop();
        Atom guiltyAtom;
        if (assertedAtoms.size() < 1)
            return true;
        Atom marker = assertedAtoms.pop();
        do {
            guiltyAtom = marker;
            if (assertedAtoms.size() < 1)
                return true;
            marker = assertedAtoms.pop();
        } while (!(marker instanceof Marker));
        guiltyAtom = invert((Bound) guiltyAtom);
        assertedAtoms.push(guiltyAtom);
        Map<String, Interval> tempMap = intervalAssignmentStack.pop();
        Map<String, Interval> updateGuiltyVariable = update(guiltyAtom, tempMap);
        tempMap.putAll(updateGuiltyVariable);
        intervalAssignmentStack.push(tempMap);
        return false;
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

}