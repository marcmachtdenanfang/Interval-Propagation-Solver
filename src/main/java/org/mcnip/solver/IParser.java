package org.mcnip.solver;

import java.util.List;
import java.util.Map;

import org.mcnip.solver.Model.Bound;
import org.mcnip.solver.Model.Formula;
import org.mcnip.solver.Model.Interval;

public interface IParser {

    /*
     * @return a String representation of the formula in CNF form.
     */
    // public String asCNF();

    /**
     * @return a Formula object of the Bounds, Pairs, Triplets and Bools generated
     * from the original input formula.
     */
    public Formula getFormula();

    /**
     * 
     * @return Map<Name of variable, its interval> of declared variables, 
     * as well as variables introduced by rewriting to three-address form.
     */
    public Map<String, Interval> getIntervals();


}
