package org.mcnip.solver.contractions

import org.mcnip.solver.Model.DotInterval
import org.mcnip.solver.Model.IPSNumber
import org.mcnip.solver.Model.IPSNumber.*
import org.mcnip.solver.Model.Interval
import org.mcnip.solver.Model.Type.INT
import org.mcnip.solver.filteredMapOf
import kotlin.math.nextDown
import kotlin.math.nextUp

class BiContractions(intervals: Map<String, Interval>, names: Array<String>) {
  private val result = names[0]
  private val fstArg = names[1]
  private val sndArg = names[2]
  private val resInterval = intervals[result]?:DotInterval(result, result)
  private val fstInterval = intervals[fstArg]?:DotInterval(fstArg, fstArg)
  private val sndInterval = intervals[sndArg]?:DotInterval(sndArg, sndArg)
  private val resLowerBound = resInterval.lowerBound
  private val resUpperBound = resInterval.upperBound
  private val fstLowerBound = fstInterval.lowerBound
  private val fstUpperBound = fstInterval.upperBound
  private val sndLowerBound = sndInterval.lowerBound
  private val sndUpperBound = sndInterval.upperBound

  companion object {
    @JvmStatic
    fun add(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to Interval(resInterval, fstLowerBound + sndLowerBound, fstUpperBound + sndUpperBound),
      fstArg to Interval(fstInterval, resLowerBound - sndUpperBound, resUpperBound - sndLowerBound, true),
      sndArg to Interval(sndInterval, resLowerBound - fstUpperBound, resUpperBound - fstLowerBound, true))
    }

    @JvmStatic
    fun sub(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to Interval(resInterval, fstLowerBound - sndUpperBound, fstUpperBound - sndLowerBound, true),
          fstArg to Interval(fstInterval, resLowerBound + sndLowerBound, resUpperBound + sndUpperBound),
          sndArg to Interval(sndInterval, fstLowerBound - resUpperBound, fstUpperBound - resLowerBound, true))
    }

    @JvmStatic
    fun mul(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to resInterval.withMul(fstInterval, sndInterval), fstArg to fstInterval.withDiv(resInterval, sndInterval), sndArg to sndInterval.withDiv(resInterval, fstInterval))
    }

    @JvmStatic
    fun div(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to resInterval.withDiv(fstInterval, sndInterval), fstArg to fstInterval.withMul(resInterval, sndInterval), sndArg to sndInterval.withDiv(fstInterval, resInterval))
    }

    @JvmStatic
    fun pow(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to resInterval.withPow(fstInterval, sndInterval), fstArg to fstInterval.withNrt(resInterval, sndInterval), sndArg to sndInterval)
    }

    @JvmStatic
    fun nrt(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to resInterval.withNrt(fstInterval, sndInterval), fstArg to fstInterval.withPow(resInterval, sndInterval), sndArg to sndInterval)
    }

    @JvmStatic
    fun min(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to Interval(resInterval, fstLowerBound.min(sndLowerBound), fstUpperBound.min(sndUpperBound)),
          fstArg to Interval(fstInterval, resLowerBound, POS_INF),
          sndArg to Interval(sndInterval, resLowerBound, POS_INF))
    }

    @JvmStatic
    fun max(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      filteredMapOf(result to Interval(resInterval, fstLowerBound.max(sndLowerBound), fstUpperBound.max(sndUpperBound)),
          fstArg to Interval(fstInterval, NEG_INF, resUpperBound),
          sndArg to Interval(sndInterval, NEG_INF, resUpperBound))
    }
  }

  private fun Interval.withMul(i0: Interval, i1: Interval) = withOp(IPSNumber::mul, i0, i1)
  private fun Interval.withPow(base: Interval, power: Interval) = withCautionOp(IPSNumber::pow, base, power)
  private fun Interval.withNrt(num: Interval, root: Interval) = withCautionOp(IPSNumber::nrt, num, root)

  private fun Interval.withDiv(numerator: Interval, denominator: Interval) = when {
    type != INT && denominator.lowerBound.fpValue <= 0.0.nextUp() && denominator.upperBound.fpValue >= 0.0.nextDown() ->
      this
    numerator.lowerBound.isInfinite && numerator.upperBound.isInfinite ->
      this
    type == INT && denominator.lowerBound == ZERO_int && denominator.upperBound == ZERO_int ->
      this
    type != INT || denominator.lowerBound > ZERO_int || denominator.upperBound < ZERO_int ->
      withOp(IPSNumber::div, numerator, denominator)
    denominator.lowerBound == ZERO_int ->
      withOp(IPSNumber::div, numerator, Interval(denominator.varName, ONE_int, denominator.upperBound))
    denominator.upperBound == ZERO_int ->
      withOp(IPSNumber::div, numerator, Interval(denominator.varName, denominator.lowerBound, NEG_ONE_int))
    else ->
      Interval(withOp(IPSNumber::div, numerator, denominator), numerator.lowerBound.min(-numerator.upperBound), numerator.upperBound.max(-numerator.lowerBound))
  }

  private fun Interval.withOp(op: (IPSNumber, IPSNumber) -> IPSNumber, i0: Interval, i1: Interval) = Triple(op, i0, i1).let { Interval(this, it.min(), it.max(), true) }

  private fun Interval.withCautionOp(op: (IPSNumber, IPSNumber) -> IPSNumber, i0: Interval, i1: Interval) = when {
    i1.upperBound.intValue.signum() < 1 ->
      Interval(this, i0.lowerBound, i0.upperBound)
    i1.lowerBound.intValue.signum() < 1 ->
      withOp(op, i0, Interval(i1.varName, if (type != INT) ONE else ONE_int, i1.upperBound))
    op != IPSNumber::pow && i0.lowerBound < ZERO_int && i1.lowerBound.intValue == i1.upperBound.intValue && i1.lowerBound.intValue.toInt() % 2 == 0 ->
      withOp(op, i0, i1).run { Interval(this, ZERO_int, upperBound) }
    else ->
      withOp(op, i0, i1)
  }

  private fun Triple<(IPSNumber, IPSNumber) -> IPSNumber, Interval, Interval>.min() = minmax(first, second, third, IPSNumber::min)
  private fun Triple<(IPSNumber, IPSNumber) -> IPSNumber, Interval, Interval>.max() = minmax(first, second, third, IPSNumber::max)

  private fun minmax(op: IPSNumber.(IPSNumber) -> IPSNumber, i0: Interval, i1: Interval, m: (IPSNumber, IPSNumber) -> IPSNumber): IPSNumber {
    return m(m(m(i0.lowerBound.op(i1.lowerBound), i0.lowerBound.op(i1.upperBound)), i0.upperBound.op(i1.lowerBound)), i0.upperBound.op(i1.upperBound))
  }

}