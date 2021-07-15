package org.mcnip.solver.contractions

import org.mcnip.solver.Model.DotInterval
import org.mcnip.solver.Model.IPSNumber
import org.mcnip.solver.Model.Interval
import org.mcnip.solver.filteredMapOf

class BoundContractions(private val intervals: Map<String, Interval>, names: Array<String>) {
  private val base = names[0]
  private val limiter = names[1]
  private val baseInterval = intervals[base]?:DotInterval(base, base)
  private val limiterInterval = intervals[limiter]?:DotInterval(limiter, limiter)
  private val baseLowerBound = baseInterval.lowerBound
  private val baseUpperBound = baseInterval.upperBound
  private val limiterLowerBound = limiterInterval.lowerBound
  private val limiterUpperBound = limiterInterval.upperBound
  private fun fold(operation: (Boolean, Interval) -> Boolean) = intervals.values.fold(false, operation)
  private fun lowerLimit() = { acc: Boolean, interval: Interval -> acc || interval.lowerBound.fpValue.isInfinite() }
  private fun upperLimit() = { acc: Boolean, interval: Interval -> acc || interval.upperBound.fpValue.isInfinite() }

  companion object {
    @JvmStatic
    fun equals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      filteredMapOf(base to Interval(baseInterval, limiterLowerBound, limiterUpperBound, false), limiter to Interval(limiterInterval, baseLowerBound, baseUpperBound, false))
    }

    @JvmStatic
    fun greater(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      if (fold(upperLimit())) intervals else filteredMapOf(base to Interval(baseInterval, limiterUpperBound.inc(), baseUpperBound, false), limiter to limiterInterval)
    }

    @JvmStatic
    fun greaterEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      if (fold(upperLimit())) intervals else filteredMapOf(base to Interval(baseInterval, limiterUpperBound, baseUpperBound, false), limiter to limiterInterval)
    }

    @JvmStatic
    fun less(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      if (fold(lowerLimit())) intervals else filteredMapOf(base to Interval(baseInterval, baseLowerBound, limiterLowerBound.dec(), false), limiter to limiterInterval)
    }

    @JvmStatic
    fun lessEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      if (fold(lowerLimit())) intervals else filteredMapOf(base to Interval(baseInterval, baseLowerBound, limiterLowerBound, false), limiter to limiterInterval)
    }

    @JvmStatic
    fun notEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      if (fold { acc, it -> acc || (it.lowerBound.fpValue.isInfinite() && it.upperBound.fpValue.isInfinite()) }) intervals else filteredMapOf(base to when {
        limiterUpperBound < baseLowerBound || baseUpperBound < limiterLowerBound || (baseLowerBound < limiterLowerBound && limiterUpperBound < baseUpperBound) ->
          baseInterval
        limiterLowerBound <= baseLowerBound && baseUpperBound <= limiterUpperBound ->
          Interval(base, IPSNumber(1, baseLowerBound.type), IPSNumber(0, baseLowerBound.type))
        limiterLowerBound < baseLowerBound ->
          Interval(base, limiterUpperBound.inc(), baseUpperBound)
        else ->
          Interval(base, baseLowerBound, limiterLowerBound.dec())
      }, limiter to limiterInterval)
    }
  }

}