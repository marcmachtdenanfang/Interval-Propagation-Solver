package org.mcnip.solver.Contractors.BinContractor;

import java.util.HashMap;
import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.Interval;

public class AddContractor implements Contractor{
    
    public AddContractor() {}
    
    /**
     * @param in Map containing the Intervals of the variables in names.
     * @param names First value should be the result, second and third should be the adders.
     */
    public Map<String, Interval> contract(Map<String, Interval> in, String[] names)
    {
        Map<String, Interval> resultIntervals = new HashMap<>();
        
        String res  = names[0];
        String add1 = names[1];
        String add2 = names[2];

        // hande result interval:
        int resLowerBound = Math.max(
                in.get(res).getLowerBound(),
                in.get(add1).getLowerBound() + in.get(add2).getLowerBound()
            );
        int resUpperBound = Math.min(
                in.get(res).getUpperBound(),
                in.get(add1).getUpperBound() + in.get(add2).getUpperBound() 
            );
        Interval j = new Interval(res, resLowerBound, resUpperBound, true, true);
        resultIntervals.put(res, j);


        // handle add1 interval:
        int add1LowerBound = Math.max(
                in.get(res).getLowerBound() - in.get(add2).getUpperBound(),
                in.get(add1).getLowerBound()     
            );
        int add1UpperBound = Math.max(
                in.get(res).getUpperBound() - in.get(add2).getLowerBound(),
                in.get(add1).getUpperBound()     
            );
        Interval j1 = new Interval(add1, add1LowerBound, add1UpperBound, true, true);
        resultIntervals.put(add1, j1);


        // handle add2 interval:
        int add2LowerBound = Math.max(
                in.get(res).getLowerBound() - in.get(add1).getUpperBound(),
                in.get(add2).getLowerBound()     
            );
        int add2UpperBound = Math.max(
                in.get(res).getUpperBound() - in.get(add1).getLowerBound(),
                in.get(add2).getUpperBound()     
            );
        Interval j2 = new Interval(add2, add2LowerBound, add2UpperBound, true, true);
        resultIntervals.put(add2, j2);

        return resultIntervals;
    }


}
