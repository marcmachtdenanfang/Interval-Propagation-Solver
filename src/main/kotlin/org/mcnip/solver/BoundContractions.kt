package org.mcnip.solver

import org.mcnip.solver.Model.IPSNumber
import org.mcnip.solver.Model.Interval
import org.mcnip.solver.Model.Type

class BoundContractions(intervals: Map<String, Interval>, names: Array<String>) {
  val base = names[0]
  val limiter = names[1]
  val baseInterval = intervals.getValue(base)
  val limiterInterval = intervals.getValue(limiter)
  val baseLowerBound: IPSNumber = baseInterval.lowerBound
  val baseUpperBound: IPSNumber = baseInterval.upperBound
  val limiterLowerBound: IPSNumber = limiterInterval.lowerBound
  val limiterUpperBound: IPSNumber = limiterInterval.upperBound
  val type: Type = baseLowerBound.type

  companion object {
    @JvmStatic
    fun equals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      mutableMapOf(limiter to limiterInterval, base to when {
        limiterLowerBound <= baseLowerBound && baseUpperBound <= limiterUpperBound ->
          baseInterval
        baseLowerBound < limiterLowerBound && limiterUpperBound < baseUpperBound ->
          Interval(base, limiterLowerBound, limiterUpperBound)
        limiterUpperBound < baseLowerBound && baseUpperBound < limiterLowerBound ->
          Interval(base, IPSNumber(1, type), IPSNumber(0, type))
        limiterLowerBound < baseLowerBound ->
          Interval(base, limiterLowerBound, baseUpperBound)
        else ->
          Interval(base, baseLowerBound, limiterUpperBound)
      })
    }

    @JvmStatic
    fun greater(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      mutableMapOf(limiter to limiterInterval, base to when {
        limiterUpperBound < baseLowerBound ->
          baseInterval
        baseUpperBound <= limiterUpperBound ->
          Interval(base, IPSNumber(1, type), IPSNumber(0, type))
        else ->
          Interval(base, limiterUpperBound.inc(), baseUpperBound)
      })
    }

    @JvmStatic
    fun greaterEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      mutableMapOf(limiter to limiterInterval, base to when {
        limiterUpperBound <= baseLowerBound ->
          baseInterval
        baseUpperBound < limiterUpperBound ->
          Interval(base, IPSNumber(1, type), IPSNumber(0, type))
        else ->
          Interval(base, limiterUpperBound, baseUpperBound)
      })
    }

    @JvmStatic
    fun less(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      mutableMapOf(limiter to limiterInterval, base to when {
        baseUpperBound < limiterLowerBound ->
          baseInterval
        limiterLowerBound <= baseLowerBound ->
          Interval(base, IPSNumber(1, type), IPSNumber(0, type))
        else ->
          Interval(base, baseLowerBound, limiterLowerBound.dec())
      })
    }

    @JvmStatic
    fun lessEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      mutableMapOf(limiter to limiterInterval, base to when {
        baseUpperBound <= limiterLowerBound ->
          baseInterval
        limiterLowerBound < baseLowerBound ->
          Interval(base, IPSNumber(1, type), IPSNumber(0, type))
        else ->
          Interval(base, baseLowerBound, limiterLowerBound)
      })
    }

    @JvmStatic
    fun notEquals(intervals: Map<String, Interval>, names: Array<String>) = BoundContractions(intervals, names).run {
      mutableMapOf(limiter to limiterInterval, base to when {
        limiterUpperBound < baseLowerBound || baseUpperBound < limiterLowerBound || (baseLowerBound < limiterLowerBound && limiterUpperBound < baseUpperBound) ->
          baseInterval
        limiterLowerBound <= baseLowerBound && baseUpperBound <= limiterUpperBound ->
          Interval(base, IPSNumber(1, type), IPSNumber(0, type))
        limiterLowerBound < baseLowerBound ->
          Interval(base, limiterUpperBound.inc(), baseUpperBound)
        else ->
          Interval(base, baseLowerBound, limiterLowerBound.dec())
      })
    }
  }

}