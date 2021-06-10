package org.mcnip.solver.Model;

import java.math.BigInteger;

public class DotInterval extends Interval {
    
    public DotInterval(String name, int bound)
    {
        // a dot-interval is always a closed interval,
        // therefore the booleans are true.
        super(name, bound, bound, true, true);
    }

    public DotInterval(String name, double bound)
    {
        super(name, Math.nextDown(bound), Math.nextUp(bound), true, true);
    }

    public DotInterval(String name, IPSNumber bound)
    {
        super(name, bound, bound);
    }

    public DotInterval(String name, BigInteger bound)
    {
        super(name, bound, bound, true, true);
    }

    public DotInterval(String name, String bound) {
        super(name, bound, bound);
    }
}
