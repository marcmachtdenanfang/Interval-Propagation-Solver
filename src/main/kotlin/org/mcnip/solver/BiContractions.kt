package org.mcnip.solver

import org.mcnip.solver.Model.IPSNumber
import org.mcnip.solver.Model.Interval

class BiContractions(intervals: Map<String, Interval>, names: Array<String>) {
  private val result = names[0]
  private val fstArg = names[1]
  private val sndArg = names[2]
  private val resInterval = intervals.getValue(result)
  private val fstInterval = intervals.getValue(fstArg)
  private val sndInterval = intervals.getValue(sndArg)
  private val resLowerBound: IPSNumber = resInterval.lowerBound
  private val resUpperBound: IPSNumber = resInterval.upperBound
  private val fstLowerBound: IPSNumber = fstInterval.lowerBound
  private val fstUpperBound: IPSNumber = fstInterval.upperBound
  private val sndLowerBound: IPSNumber = sndInterval.lowerBound
  private val sndUpperBound: IPSNumber = sndInterval.upperBound

  companion object {
    @JvmStatic
    fun add(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to Interval(resInterval, fstLowerBound + sndLowerBound, fstUpperBound + sndUpperBound, false),
      fstArg to Interval(fstInterval, resLowerBound - sndUpperBound, resUpperBound - sndLowerBound, true),
      sndArg to Interval(sndInterval, resLowerBound - fstUpperBound, resUpperBound - fstLowerBound, true))
    }

    @JvmStatic
    fun sub(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to Interval(resInterval, fstLowerBound - sndUpperBound, fstUpperBound - sndLowerBound, true),
          fstArg to Interval(fstInterval, resLowerBound + sndLowerBound, resUpperBound + sndUpperBound, false),
          sndArg to Interval(sndInterval, fstLowerBound - resUpperBound, fstUpperBound - resLowerBound, true))
    }

    @JvmStatic
    fun mul(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to resInterval.withMul(fstInterval, sndInterval), fstArg to fstInterval.withDiv(resInterval, sndInterval), sndArg to sndInterval.withDiv(resInterval, fstInterval))
    }

    @JvmStatic
    fun div(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to resInterval.withDiv(fstInterval, sndInterval), fstArg to fstInterval.withMul(resInterval, sndInterval), sndArg to sndInterval.withDiv(fstInterval, resInterval))
    }

    @JvmStatic
    fun pow(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to resInterval.withPow(fstInterval, sndInterval), fstArg to fstInterval.withNrt(resInterval, sndInterval), sndArg to sndInterval)
    }

    @JvmStatic
    fun nrt(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to resInterval.withNrt(fstInterval, sndInterval), fstArg to fstInterval.withPow(resInterval, sndInterval), sndArg to sndInterval)
    }

    @JvmStatic
    fun min(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to Interval(resInterval, fstLowerBound.min(sndLowerBound), fstUpperBound.min(sndUpperBound), false), fstArg to fstInterval, sndArg to sndInterval)
    }

    @JvmStatic
    fun max(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mapOf(result to Interval(resInterval, fstLowerBound.max(sndLowerBound), fstUpperBound.max(sndUpperBound), false), fstArg to fstInterval, sndArg to sndInterval)
    }
  }

  private fun Interval.withMul(i0: Interval, i1: Interval) = withOp(IPSNumber::mul, i0, i1)
  private fun Interval.withDiv(numerator: Interval, denominator: Interval) = withCautionOp(IPSNumber::div, numerator, denominator)
  private fun Interval.withPow(base: Interval, power: Interval) = withOp(IPSNumber::pow, base, power)
  private fun Interval.withNrt(num: Interval, root: Interval) = withCautionOp(IPSNumber::nrt, num, root)

  private fun Interval.withOp(op: (IPSNumber, IPSNumber) -> IPSNumber, i0: Interval, i1: Interval) = Triple(op, i0, i1).let { Interval(this, it.min(), it.max(), true) }

  private fun Interval.withCautionOp(op: (IPSNumber, IPSNumber) -> IPSNumber, i0: Interval, i1: Interval) =
      if (i1.lowerBound.isZeroOrInfinite)
        if (i1.upperBound.isZeroOrInfinite)
          this
        else
          oneSidedIntervalOp(op, i0, i1.upperBound)
      else
        if (i1.upperBound.isZeroOrInfinite)
          oneSidedIntervalOp(op, i0, i1.lowerBound)
        else
          withOp(op, i0, i1)

  private fun oneSidedIntervalOp(op: IPSNumber.(IPSNumber) -> IPSNumber, interval: Interval, number: IPSNumber) =
      listOf(IPSNumber::min, IPSNumber::max).map { it(interval.lowerBound.op(number), interval.upperBound.op(number)) }.run {
        Interval(interval, first(), last(), true)
      }

  private fun Triple<(IPSNumber, IPSNumber) -> IPSNumber, Interval, Interval>.min() = minmax(first, second, third, IPSNumber::min)
  private fun Triple<(IPSNumber, IPSNumber) -> IPSNumber, Interval, Interval>.max() = minmax(first, second, third, IPSNumber::max)

  private fun minmax(op: IPSNumber.(IPSNumber) -> IPSNumber, i0: Interval, i1: Interval, m: (IPSNumber, IPSNumber) -> IPSNumber): IPSNumber {
    return m(m(m(i0.lowerBound.op(i1.lowerBound), i0.lowerBound.op(i1.upperBound)), i0.upperBound.op(i1.lowerBound)), i0.upperBound.op(i1.upperBound))
  }

}