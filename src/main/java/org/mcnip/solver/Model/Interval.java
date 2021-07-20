package org.mcnip.solver.Model;

import java.math.BigInteger;

import org.jetbrains.annotations.NotNull;

import lombok.Setter;


public class Interval {

    private final String varName;
    @Setter private IPSNumber lowerBound;
    @Setter private IPSNumber upperBound;

    @Override
    public String toString()
    {
        return varName + " := [" + lowerBound.toString() + ", " + upperBound.toString() + "]";
    }

    public Interval(String name, Type t)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(Double.NEGATIVE_INFINITY, t);
        this.upperBound = new IPSNumber(Double.POSITIVE_INFINITY, t);
    }

    /**
     * Constructor for boolean Intervals.
     * @param name Name of variable
     * @param posPolarity Polarity of boolean variable is positive (true) or negative (false).
     */
    public Interval(String name, boolean posPolarity)
    {
        this.varName = name;
        if(posPolarity) {
            this.lowerBound = new IPSNumber(1, Type.BOOL);
            this.upperBound = new IPSNumber(Double.POSITIVE_INFINITY, Type.BOOL);
        } else {
            this.lowerBound = new IPSNumber(Double.NEGATIVE_INFINITY, Type.BOOL);
            this.upperBound = new IPSNumber(0, Type.BOOL);
        }
    }

    public Interval(String name, String lowerBound, String upperBound) {
        this.varName = name;
        this.lowerBound = new IPSNumber(lowerBound, false);
        this.upperBound = new IPSNumber(upperBound, true);
    }

    public Interval(String name, String lowerBound, String upperBound, boolean lowerIsClosed, boolean upperIsClosed) {
        this.varName = name;
        if (name.contains(".")) {
            this.lowerBound = new IPSNumber(lowerIsClosed ? Math.nextDown(Double.parseDouble(lowerBound)) : Math.nextUp(Double.parseDouble(lowerBound)), Type.REAL);
            this.upperBound = new IPSNumber(upperIsClosed ? Math.nextUp(Double.parseDouble(upperBound)) : Math.nextDown(Double.parseDouble(upperBound)), Type.REAL);
        }
        else {
            this.lowerBound = new IPSNumber(lowerIsClosed ? new BigInteger(lowerBound) : new BigInteger(lowerBound).add(BigInteger.valueOf(1)), Type.INT);
            this.upperBound = new IPSNumber(upperIsClosed ? new BigInteger(upperBound) : new BigInteger(upperBound).subtract(BigInteger.valueOf(1)), Type.INT);
        }
    }

    public Interval(
        String name,
        int lowerBound,
        int upperBound,
        boolean lowerIsClosed,
        boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(lowerIsClosed ? lowerBound : lowerBound + 1, Type.INT);
        this.upperBound = new IPSNumber(upperIsClosed ? upperBound : upperBound - 1, Type.INT);
    }

    public Interval(
        String name,
        IPSNumber lowerBound,
        IPSNumber upperBound)
    {
        this.varName = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Interval(
        Interval result,
        IPSNumber altLowerBound,
        IPSNumber altUpperBound,
        Boolean doPadding)
    {
        this.varName = result.varName;
        this.lowerBound = result.lowerBound.max(doPadding ? altLowerBound.padDown() : altLowerBound);
        this.upperBound = result.upperBound.min(doPadding ? altUpperBound.padUp() : altUpperBound);
    }

    public Interval(
        Interval result,
        Double altLowerBound,
        Double altUpperBound)
    {
        this.varName = result.varName;
        this.lowerBound = result.lowerBound.max(new IPSNumber(altLowerBound, Type.REAL));
        this.upperBound = result.upperBound.min(new IPSNumber(altUpperBound, Type.REAL));
    }

    public Interval(
        String name,
        double lowerBound,
        double upperBound,
        boolean lowerIsClosed,
        boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(lowerIsClosed ? Math.nextDown(lowerBound) : Math.nextUp(lowerBound), Type.REAL);
        this.upperBound = new IPSNumber(upperIsClosed ? Math.nextUp(upperBound) : Math.nextDown(upperBound), Type.REAL);
    }

    public Interval(
        String name,
        BigInteger lowerBound,
        BigInteger upperBound,
        boolean lowerIsClosed,
        boolean upperIsClosed)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(lowerIsClosed ? lowerBound : lowerBound.add(BigInteger.valueOf(1)), Type.INT);
        this.upperBound = new IPSNumber(upperIsClosed ? upperBound : lowerBound.subtract(BigInteger.valueOf(1)), Type.INT);
    }

    public String getVarName() {
        return varName;
    }

    public IPSNumber getLowerBound() {
        return lowerBound;
    }

    public IPSNumber getUpperBound() {
        return upperBound;
    }

    public IPSNumber getMidPoint() {
        if(this.upperBound.equals(IPSNumber.POS_INF) && this.lowerBound.equals(IPSNumber.NEG_INF)) {
            return new IPSNumber(0, lowerBound.getType());
        } else if(this.upperBound.equals(IPSNumber.POS_INF)) {
            if(this.upperBound.getType() == Type.INT) {
                // (1l<<63)-1 == Long.Max_Value
                BigInteger big = BigInteger.valueOf(Long.MAX_VALUE);
                IPSNumber temp = this.lowerBound.add(new IPSNumber(big, Type.INT));
                return temp.div(new IPSNumber(2, Type.INT));
            }
            // for intervals [a, inf], the midpoint is Real.MAX_VALUE, IEEE P1788
            return IPSNumber.POS_INF;
        } else if(this.lowerBound.equals(IPSNumber.NEG_INF)) {
            if(this.lowerBound.getType() == Type.INT) {
                // (1l<<63)-1 == Long.Max_Value
                BigInteger big = BigInteger.valueOf(Long.MIN_VALUE);
                IPSNumber temp = this.upperBound.add(new IPSNumber(big, Type.INT));
                return temp.div(new IPSNumber(2, Type.INT));
            }
            // for intervals [-inf, a], the midpoint is Real.MIN_VALUE, IEEE P1788
            return IPSNumber.NEG_INF;
        }
        IPSNumber temp = this.upperBound.add(this.lowerBound);
        return temp.div(new IPSNumber(2, this.lowerBound.getType()));
    }

    /**
     * Internal representation for empty Interval is
     * lowerBound > upperBound.
     *
     * @return true if interval is empty.
     */
    public boolean isEmpty()
    {
        return this.lowerBound.gt(this.upperBound);
    }

    public boolean containsMoreThanOneValue() {
        // since we always have closed intervals, this code suffices.
        if(lowerBound.equals(upperBound)) return false;
        return true;
    }
    

    public boolean isDotInfinite() {
        if(this.lowerBound.equals(this.upperBound)) {
            return this.lowerBound.isInfinite();
        }
        return false;
    }

    public Type getType() {
        return this.lowerBound.getType();
    }

    public void setType(Type type) {
        this.lowerBound.setType(type);
        this.upperBound.setType(type);
    }

}
