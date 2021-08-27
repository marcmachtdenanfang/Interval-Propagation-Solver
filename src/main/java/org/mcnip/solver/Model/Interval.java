package org.mcnip.solver.Model;

import java.math.BigInteger;

import lombok.Setter;


public class Interval {

    private final String varName;
    @Setter private IPSNumber lowerBound;
    @Setter private IPSNumber upperBound;

    @Override
    public String toString()
    {
        return this.toString(false);
    }

    public String toString(Boolean precise)
    {
        if (isNumeric())
            return varName;
        else if (lowerBound.equals(upperBound))
            return varName + " := " + lowerBound;
        else if (precise || this.containsMoreThanOneValue())
            return varName + " in [" + lowerBound + ", " + upperBound + "]";
        else
            return varName + " ~= " + this.getMidPoint(64);
    }

    public Interval(String name, Type t)
    {
        this.varName = name;
        this.lowerBound = new IPSNumber(Double.NEGATIVE_INFINITY, t);
        this.upperBound = new IPSNumber(Double.POSITIVE_INFINITY, t);
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
        IPSNumber altLowerBound,
        IPSNumber altUpperBound)
    {
        this.varName = result.varName;
        this.lowerBound = result.lowerBound.max(altLowerBound);
        this.upperBound = result.upperBound.min(altUpperBound);
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

    private static String genBitString(int length) {
        return "1".repeat(length);
    }

    public IPSNumber getMidPoint(int bitPrecision) {
        //double lower = (this.lowerBound.getIntValue() == null || this.lowerBound.getIntValue().signum() != 0) ? Math.nextUp(this.lowerBound.getFpValue()) / 2.0 : this.lowerBound.getFpValue();
        //double upper = (this.upperBound.getIntValue() == null || this.upperBound.getIntValue().signum() != 0) ? Math.nextDown(this.upperBound.getFpValue()) / 2.0 : this.upperBound.getFpValue();
        //Double random = lower + Math.random() * (upper - lower);
        //return new IPSNumber(random, getType());
        if(this.upperBound.equals(IPSNumber.POS_INF) && this.lowerBound.equals(IPSNumber.NEG_INF)) {
            return new IPSNumber(0, lowerBound.getType());
        } else if(this.upperBound.equals(IPSNumber.POS_INF)) {
            if(this.upperBound.getType() == Type.INT) {
                // (1l<<63)-1 == Long.Max_Value
                BigInteger big = new BigInteger(genBitString(bitPrecision), 2);
                // BigInteger big = BigInteger.valueOf(Long.MAX_VALUE);
                IPSNumber temp = this.lowerBound.add(new IPSNumber(big, Type.INT));
                return temp.div(new IPSNumber(2, Type.INT));
            }
            // for intervals [a, inf], the midpoint is Real.MAX_VALUE, IEEE P1788
            return new IPSNumber(Double.MAX_VALUE, Type.REAL);
        } else if(this.lowerBound.equals(IPSNumber.NEG_INF)) {
            if(this.lowerBound.getType() == Type.INT) {
                // (-1l<<63) == Long.MIN_Value
                BigInteger big = new BigInteger("-" + genBitString(bitPrecision), 2);
                // BigInteger big = BigInteger.valueOf(Long.MIN_VALUE);
                IPSNumber temp = this.upperBound.add(new IPSNumber(big, Type.INT));
                return temp.div(new IPSNumber(2, Type.INT));
            }
            // for intervals [-inf, a], the midpoint is Real.MIN_VALUE, IEEE P1788
            return new IPSNumber(Double.MIN_VALUE, Type.REAL);
        }
        if(this.lowerBound.getType() == Type.REAL && lowerBound.add(upperBound).isInfinite()) {
            IPSNumber tlower = lowerBound.div(new IPSNumber(2.0, Type.REAL));
            IPSNumber tupper = upperBound.div(new IPSNumber(2.0, Type.REAL));
            return tlower.add(tupper);
        }
        IPSNumber temp = this.upperBound.add(this.lowerBound);
        IPSNumber temp2 = temp.div(new IPSNumber(2, this.lowerBound.getType()));
        if(this.lowerBound.getType() == Type.INT && this.lowerBound.lt(IPSNumber.ZERO_int) && temp2.lt(IPSNumber.ZERO_int)) {
            return temp2.sub(IPSNumber.ONE_int);
        }
        // if(this.lowerBound.getType() == Type.REAL) return temp2.nextUp();
        return temp2;
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
        // additional padding should be used for floats since they might not be exact
        else return this.getType() == Type.INT || lowerBound.nextUp().nextUp().nextUp().lt(upperBound);
    }

    public boolean isDotInfinite() {
        if (this.lowerBound.equals(this.upperBound))
            return this.lowerBound.isInfinite();
        return false;
    }

    public Type getType() {
        return this.lowerBound.getType();
    }

    public void setType(Type type) {
        this.lowerBound.setType(type);
        this.upperBound.setType(type);
    }

    public boolean isNumeric() {
        return this.varName.charAt(0) == '-' || Character.isDigit(this.varName.charAt(0));
    }

}
