package org.mcnip.solver.contractions

import org.mcnip.solver.Model.DotInterval
import org.mcnip.solver.Model.IPSNumber
import org.mcnip.solver.Model.Interval
import org.mcnip.solver.filteredMapOf

class BoundContractions(intervals: Map<String, Interval>, names: Array<String>) {
  private val base = names[0]
  private val limiter = names[1]
  private val baseInterval = intervals[base]?:DotInterval(base, base)
  private val limiterInterval = intervals[limiter]?:DotInterval(limiter, limiter)
  private val baseLowerBound: IPSNumber = baseInterval.lowerBound
  private val baseUpperBound: IPSNumber = baseInterval.upperBound
  private val limiterLowerBound: IPSNumber = limiterInterval.lowerBound
  private val limiterUpperBound: IPSNumber = limiterInterval.upperBound

  companion object {
    @JvmStatic
    fun equals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      filteredMapOf(base to Interval(baseInterval, limiterLowerBound, limiterUpperBound, false), limiter to Interval(limiterInterval, baseLowerBound, baseUpperBound, false))
    }

    @JvmStatic
    fun greater(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      filteredMapOf(base to Interval(baseInterval, limiterUpperBound.inc(), baseUpperBound, false), limiter to limiterInterval)
    }

    @JvmStatic
    fun greaterEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      filteredMapOf(base to Interval(baseInterval, limiterUpperBound, baseUpperBound, false), limiter to limiterInterval)
    }

    @JvmStatic
    fun less(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      filteredMapOf(base to Interval(baseInterval, baseLowerBound, limiterLowerBound.dec(), false), limiter to limiterInterval)
    }

    @JvmStatic
    fun lessEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      filteredMapOf(base to Interval(baseInterval, baseLowerBound, limiterLowerBound, false), limiter to limiterInterval)
    }

    @JvmStatic
    fun notEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      filteredMapOf(base to when {
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