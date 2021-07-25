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
  private val type = resInterval.type

  companion object {
    @JvmStatic
    fun add(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = Interval(resInterval, fstLowerBound + sndLowerBound, fstUpperBound + sndUpperBound)
      filteredMapOf(result to newResult,
      fstArg to Interval(fstInterval, newResult.lowerBound - sndUpperBound, newResult.upperBound - sndLowerBound, true),
      sndArg to Interval(sndInterval, newResult.lowerBound - fstUpperBound, newResult.upperBound - fstLowerBound, true))
    }

    @JvmStatic
    fun sub(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = Interval(resInterval, fstLowerBound - sndUpperBound, fstUpperBound - sndLowerBound, true)
      filteredMapOf(result to newResult,
          fstArg to Interval(fstInterval, newResult.lowerBound + sndLowerBound, newResult.upperBound + sndUpperBound),
          sndArg to Interval(sndInterval, fstLowerBound - newResult.upperBound, fstUpperBound - newResult.lowerBound, true))
    }

    @JvmStatic
    fun mul(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = resInterval.withMul(fstInterval, sndInterval)
      filteredMapOf(result to newResult, fstArg to fstInterval.withDiv(newResult, sndInterval), sndArg to sndInterval.withDiv(newResult, fstInterval))
    }

    @JvmStatic
    fun div(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = resInterval.withDiv(fstInterval, sndInterval)
      filteredMapOf(result to newResult, fstArg to fstInterval.withMul(newResult, sndInterval), sndArg to sndInterval.withDiv(fstInterval, newResult))
    }

    @JvmStatic
    fun pow(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = resInterval.withPow(fstInterval, sndInterval)
      filteredMapOf(result to newResult, fstArg to (if (type != INT) fstInterval else fstInterval.withNrt(newResult, sndInterval)), sndArg to sndInterval)
    }

    @JvmStatic
    fun nrt(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = resInterval.withNrt(fstInterval, sndInterval)
      filteredMapOf(result to newResult, fstArg to (if (type != INT) fstInterval else fstInterval.withPow(newResult, sndInterval)), sndArg to sndInterval)
    }

    @JvmStatic
    fun min(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = Interval(resInterval, fstLowerBound.min(sndLowerBound), fstUpperBound.min(sndUpperBound))
      filteredMapOf(result to newResult, fstArg to Interval(fstInterval, resLowerBound, POS_INF), sndArg to Interval(sndInterval, resLowerBound, POS_INF))
    }

    @JvmStatic
    fun max(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      val newResult = Interval(resInterval, fstLowerBound.max(sndLowerBound), fstUpperBound.max(sndUpperBound))
      filteredMapOf(result to newResult, fstArg to Interval(fstInterval, NEG_INF, resUpperBound), sndArg to Interval(sndInterval, NEG_INF, resUpperBound))
    }
  }

  private fun Interval.withMul(multiplier: Interval, multiplicand: Interval) = withOp(IPSNumber::mul, multiplier, multiplicand)
  private fun Interval.withPow(base: Interval, exponent: Interval) = withCautionOp(IPSNumber::pow, base, exponent)
  private fun Interval.withNrt(radicand: Interval, degree: Interval) = withCautionOp(IPSNumber::nrt, radicand, degree)

  private fun Interval.withDiv(dividend: Interval, divisor: Interval) = when {
    type != INT && divisor.lowerBound.fpValue <= 0.0.nextUp() && divisor.upperBound.fpValue >= 0.0.nextDown() ->
      this
    dividend.lowerBound.isInfinite && dividend.upperBound.isInfinite ->
      this
    type == INT && divisor.lowerBound == ZERO_int && divisor.upperBound == ZERO_int ->
      this
    type != INT || divisor.lowerBound > ZERO_int || divisor.upperBound < ZERO_int ->
      withOp(IPSNumber::div, dividend, divisor)
    divisor.lowerBound == ZERO_int ->
      withOp(IPSNumber::div, dividend, Interval(divisor.varName, ONE_int, divisor.upperBound))
    divisor.upperBound == ZERO_int ->
      withOp(IPSNumber::div, dividend, Interval(divisor.varName, divisor.lowerBound, NEG_ONE_int))
    else ->
      Interval(withOp(IPSNumber::div, dividend, divisor), dividend.lowerBound.min(-dividend.upperBound), dividend.upperBound.max(-dividend.lowerBound))
  }

  private fun Interval.withOp(op: (IPSNumber, IPSNumber) -> IPSNumber, i0: Interval, i1: Interval) = Triple(op, i0, i1).let { Interval(this, it.min(), it.max(), true) }

  private fun Interval.withCautionOp(op: (IPSNumber, IPSNumber) -> IPSNumber, i0: Interval, i1: Interval): Interval {
    if (i0.lowerBound.isInfinite || i0.upperBound.isInfinite) return this
    val snd = Interval(i1, ONE_int, i1.upperBound)
    return when {
      snd.upperBound < ONE_int ->
        Interval(varName, ONE, ZERO)
      i0.lowerBound >= ZERO_int ->
        withOp(op, i0, snd)
      snd.lowerBound.intValue.toInt() % 2 > 0 ->
        withOp(op, i0, DotInterval(snd.varName, snd.lowerBound.intValue))
      op != IPSNumber::pow ->
        if (i0.upperBound < ZERO_int) this
        else withOp(op, Interval(i0, ZERO_int, i0.upperBound), DotInterval(snd.varName, snd.lowerBound.intValue))
      else ->
        when {
          i0.upperBound < ZERO_int ->
            withOp(op, i0, DotInterval(snd.varName, snd.lowerBound.intValue))
          else ->
            withOp(op, Interval(i0, ZERO_int, i0.upperBound), DotInterval(snd.varName, snd.lowerBound.intValue))
        }
    }//.also { println("$it = $i0 $op $i1") }
  }

  private fun Triple<(IPSNumber, IPSNumber) -> IPSNumber, Interval, Interval>.min() = minmax(first, second, third, IPSNumber::min)
  private fun Triple<(IPSNumber, IPSNumber) -> IPSNumber, Interval, Interval>.max() = minmax(first, second, third, IPSNumber::max)

  private fun minmax(op: IPSNumber.(IPSNumber) -> IPSNumber, i0: Interval, i1: Interval, m: (IPSNumber, IPSNumber) -> IPSNumber): IPSNumber {
    return m(m(m(i0.lowerBound.op(i1.lowerBound), i0.lowerBound.op(i1.upperBound)), i0.upperBound.op(i1.lowerBound)), i0.upperBound.op(i1.upperBound))
  }

}