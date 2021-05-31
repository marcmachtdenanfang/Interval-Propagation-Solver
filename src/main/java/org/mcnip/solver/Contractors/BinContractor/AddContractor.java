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
        

        // hande result interval:
        IPSNumber resLowerBound = inResLowerBound.max(inLeftLowerBound.add(inRightLowerBound));
        IPSNumber resUpperBound = inResUpperBound.min(inLeftUpperBound.add(inRightUpperBound));
        boolean temp = in.get(leftArg).isLowerIsClosed() & in.get(rightArg).isLowerIsClosed();
        boolean resLowerIsClosed;
        if(inResLowerBound.gt(inLeftLowerBound.add(inRightLowerBound))) {
            resLowerIsClosed = in.get(res).isLowerIsClosed();
        } else if(inResLowerBound.equals(inLeftLowerBound.add(inRightLowerBound))) {
            resLowerIsClosed = in.get(res).isLowerIsClosed() || temp;
        } else {
            resLowerIsClosed = temp;
        }

        boolean temp1 = in.get(leftArg).isUpperIsClosed() & in.get(rightArg).isUpperIsClosed();
        boolean resUpperIsClosed;
        if(inResUpperBound.lt(inLeftUpperBound.add(inRightUpperBound))) {
            resUpperIsClosed = in.get(res).isUpperIsClosed();
        } else if(inResUpperBound.equals(inLeftUpperBound.add(inRightUpperBound))) {
            resUpperIsClosed = in.get(res).isUpperIsClosed() || temp1;
        } else {
            resUpperIsClosed = temp1;
        }

        Interval j = new Interval(res, resLowerBound, resUpperBound, resLowerIsClosed, resUpperIsClosed);
        resultIntervals.put(res, j);


        // handle leftArg interval:
        IPSNumber leftArgLowerBound = inLeftLowerBound.max(inResLowerBound.sub(inRightUpperBound));
        IPSNumber leftArgUpperBound = inLeftUpperBound.min(inResUpperBound.sub(inRightLowerBound));
        
        boolean temp2 = in.get(res).isLowerIsClosed() & in.get(rightArg).isUpperIsClosed();
        boolean leftLowerIsClosed;
        if(inLeftLowerBound.gt(inResLowerBound.sub(inRightUpperBound))) {
            leftLowerIsClosed = in.get(leftArg).isLowerIsClosed();
        } else if(inLeftLowerBound.equals(inResLowerBound.sub(inRightUpperBound))) {
            leftLowerIsClosed = in.get(leftArg).isLowerIsClosed() || temp2;
        } else {
            leftLowerIsClosed = temp2;
        }

        boolean temp3 = in.get(res).isUpperIsClosed() & in.get(rightArg).isLowerIsClosed();
        boolean leftUpperIsClosed;
        if(inLeftUpperBound.lt(inResUpperBound.sub(inRightLowerBound))) {
            leftUpperIsClosed = in.get(leftArg).isLowerIsClosed();
        } else if(inLeftUpperBound.equals(inResUpperBound.sub(inRightLowerBound))) {
            leftUpperIsClosed = in.get(leftArg).isLowerIsClosed() || temp3;
        } else {
            leftUpperIsClosed = temp3;
        }
        
        Interval j1 = new Interval(leftArg, leftArgLowerBound, leftArgUpperBound, leftLowerIsClosed, leftUpperIsClosed);
        resultIntervals.put(leftArg, j1);


        // handle rightArg interval:
        IPSNumber rightArgLowerBound = inRightLowerBound.max(inResLowerBound.sub(inLeftUpperBound));
        IPSNumber rightArgUpperBound = inRightUpperBound.min(inResUpperBound.sub(inLeftLowerBound));

        boolean temp4 = in.get(res).isLowerIsClosed() & in.get(leftArg).isUpperIsClosed();
        boolean rightLowerIsClosed;
        if(inRightLowerBound.gt(inResLowerBound.sub(inLeftUpperBound))) {
            rightLowerIsClosed = in.get(rightArg).isLowerIsClosed();
        } else if(inRightLowerBound.equals(inResLowerBound.sub(inLeftUpperBound))) {
            rightLowerIsClosed = in.get(rightArg).isLowerIsClosed() || temp4;
        } else {
            rightLowerIsClosed = temp4;
        }

        boolean temp5 = in.get(res).isUpperIsClosed() & in.get(rightArg).isLowerIsClosed();
        boolean rightUpperIsClosed;
        if(inRightUpperBound.lt(inResUpperBound.sub(inLeftLowerBound))) {
            rightUpperIsClosed = in.get(rightArg).isLowerIsClosed();
        } else if(inRightUpperBound.equals(inResUpperBound.sub(inLeftLowerBound))) {
            rightUpperIsClosed = in.get(rightArg).isLowerIsClosed() || temp5;
        } else {
            rightUpperIsClosed = temp5;
        }

        Interval j2 = new Interval(rightArg, rightArgLowerBound, rightArgUpperBound, rightLowerIsClosed, rightUpperIsClosed);
        resultIntervals.put(rightArg, j2);

        return resultIntervals;
    }

}
