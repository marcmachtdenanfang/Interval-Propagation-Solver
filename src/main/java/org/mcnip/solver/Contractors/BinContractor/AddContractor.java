package org.mcnip.solver.Contractors.BinContractor;

import java.util.HashMap;
import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.IPSNumber;
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

        String res      = names[0];
        String leftArg  = names[1];
        String rightArg = names[2];

        IPSNumber inResLowerBound   = in.get(res).getLowerBound();
        IPSNumber inResUpperBound   = in.get(res).getUpperBound();
        IPSNumber inLeftLowerBound  = in.get(leftArg).getLowerBound();
        IPSNumber inLeftUpperBound  = in.get(leftArg).getUpperBound();
        IPSNumber inRightLowerBound = in.get(rightArg).getLowerBound();
        IPSNumber inRightUpperBound = in.get(rightArg).getUpperBound();

        


        // hande result interval:
        IPSNumber resLowerBound = inResLowerBound.max(inLeftLowerBound.add(inRightLowerBound));
        IPSNumber resUpperBound = inResUpperBound.min(inLeftUpperBound.add(inRightUpperBound));
        Interval j = new Interval(res, resLowerBound, resUpperBound, true, true);
        resultIntervals.put(res, j);


        // handle leftArg interval:
        IPSNumber leftArgLowerBound = inLeftLowerBound.max(inResLowerBound.sub(inRightUpperBound));
        IPSNumber leftArgUpperBound = inLeftUpperBound.min(inResUpperBound.sub(inRightLowerBound));
        Interval j1 = new Interval(leftArg, leftArgLowerBound, leftArgUpperBound, true, true);
        resultIntervals.put(leftArg, j1);


        // handle rightArg interval:
        IPSNumber rightArgLowerBound = inRightLowerBound.max(inResLowerBound.sub(inLeftUpperBound));
        IPSNumber rightArgUpperBound = inRightUpperBound.min(inResUpperBound.sub(inLeftLowerBound));
        Interval j2 = new Interval(rightArg, rightArgLowerBound, rightArgUpperBound, true, true);
        resultIntervals.put(rightArg, j2);

        return resultIntervals;
    }


}
