package org.mcnip.solver.Contractors.BinContractor;

import java.util.HashMap;
import java.util.Map;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Model.IPSNumber;
import org.mcnip.solver.Model.Interval;

public class AddContractor implements Contractor {

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

        if(leftArg.equals(rightArg))
        {
            // do res = 2*x; contraction.
        }

        
        IPSNumber inResLowerBound   = in.get(res).getLowerBound();
        IPSNumber inResUpperBound   = in.get(res).getUpperBound();
        IPSNumber inLeftLowerBound  = in.get(leftArg).getLowerBound();
        IPSNumber inLeftUpperBound  = in.get(leftArg).getUpperBound();
        IPSNumber inRightLowerBound = in.get(rightArg).getLowerBound();
        IPSNumber inRightUpperBound = in.get(rightArg).getUpperBound();
        
        // NumberType type = inResLowerBound.getType();

        // hande result interval:
        IPSNumber resLowerBound = inResLowerBound.max(inLeftLowerBound.add(inRightLowerBound));
        IPSNumber resUpperBound = inResUpperBound.min(inLeftUpperBound.add(inRightUpperBound));
        boolean temp = in.get(leftArg).isLowerIsClosed() & in.get(rightArg).isLowerIsClosed();
        boolean resLowerIsClosed;
        if(inResLowerBound.ge(inLeftLowerBound.add(inRightLowerBound))) {
            resLowerIsClosed = in.get(res).isLowerIsClosed() || temp;
        } else {
            resLowerIsClosed = temp;
        }

        boolean temp1 = in.get(leftArg).isUpperIsClosed() & in.get(rightArg).isUpperIsClosed();
        boolean resUpperIsClosed;
        if(inResUpperBound.ge(inLeftUpperBound.add(inRightUpperBound))) {

        }



        Interval j = new Interval(res, resLowerBound, resUpperBound, resLowerIsClosed, true);
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


    /**
     * 
     * @param type 0 = min, 1 = max
     * @param a
     * @param b
     * @param aBound
     * @param bBound
     * @return true if bound is closed, false if bound is open.
     */
    private boolean calculateBound(int type, IPSNumber a, IPSNumber b, boolean aBound, boolean bBound)
    {
        switch (type)
        {
            case 0:

                return false;
            default:
                break;
        }
        return false;
    }

}
