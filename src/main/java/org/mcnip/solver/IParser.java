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
     * @return a Formula object of the Pairs and Triples generated 
     * from the original input formula.
     */
    public Formula getFormula();

    
    /**
     * 
     * @return List of all the bound constraints, that are not 
     *         part of variable declarations.
     */
    public List<Bound> getBounds();

    /**
     * 
     * @return Map<Name of variable, its interval> of declared variables, 
     * as well as variables introduced by rewriting to three-address form.
     */
    public Map<String, Interval> getIntervals();


}
