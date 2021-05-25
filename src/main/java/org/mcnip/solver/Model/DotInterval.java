package org.mcnip.solver.Model;

public class DotInterval extends Interval {
    
    public DotInterval(String name, int bound)
    {
        // a dot-interval is always a closed interval,
        // therefore the booleans are true.
        super(name, bound, bound, true, true);
    }

    public DotInterval(String name, double bound)
    {
        super(name, bound, bound, true, true);
    }

    public DotInterval(String name, IPSNumber bound)
    {
        super(name, bound, bound, true, true);
    }
}
