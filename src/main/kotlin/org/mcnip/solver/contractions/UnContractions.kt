package org.mcnip.solver.contractions

import org.mcnip.solver.Model.DotInterval
import org.mcnip.solver.Model.DotInterval.E
import org.mcnip.solver.Model.IPSNumber.ZERO
import org.mcnip.solver.Model.Interval
import org.mcnip.solver.filteredMapOf
import kotlin.math.PI
import kotlin.math.nextDown
import kotlin.math.nextUp

class UnContractions(intervals: Map<String, Interval>, names: Array<String>) {
  private val result = names[0]
  private val argument = names[1]
  private val resInterval = intervals[result]?:DotInterval(result, result)
  private val argInterval = intervals[argument]?:DotInterval(argument, argument)
  private val resLowerBound = resInterval.lowerBound
  private val resUpperBound = resInterval.upperBound
  private val argLowerBound = argInterval.lowerBound
  private val argUpperBound = argInterval.upperBound

  companion object {
    @JvmStatic
    fun abs(intervals: Map<String, Interval>, names: Array<String>) = UnContractions(intervals, names).run {
      filteredMapOf(result to Interval(resInterval, when {
        ZERO in argLowerBound..argUpperBound ->
          ZERO
        argLowerBound > ZERO ->
          argLowerBound
        else ->
          -argUpperBound
      }, when {
        ZERO in argLowerBound..argUpperBound ->
          argUpperBound.max(-argLowerBound)
        argLowerBound > ZERO ->
          argUpperBound
        else ->
          -argLowerBound
      }), argument to Interval(argInterval, -resUpperBound, resUpperBound))
    }

    @JvmStatic
    fun neg(intervals: Map<String, Interval>, names: Array<String>) = UnContractions(intervals, names).run {
      filteredMapOf(result to Interval(resInterval, -argUpperBound, -argLowerBound), argument to Interval(argInterval, -resUpperBound, -resLowerBound))
    }

    @JvmStatic
    fun exp(intervals: Map<String, Interval>, names: Array<String>) =
        BiContractions.pow(intervals + ("_E" to E), arrayOf(names[0], "_E", names[1])).run { intervals - "_E" }

    @JvmStatic
    fun sin(intervals: Map<String, Interval>, names: Array<String>) = tri({ kotlin.math.sin(it) }, intervals, names)

    @JvmStatic
    fun cos(intervals: Map<String, Interval>, names: Array<String>) = tri({ kotlin.math.cos(it) }, intervals, names)

    private fun tri(op: (Double) -> Double, intervals: Map<String, Interval>, names: Array<String>) = UnContractions(intervals, names).run {
      val lower = op(argLowerBound.fpValue)
      val lowerRise = lower < op(argLowerBound.fpValue.nextUp())
      val upper = op(argUpperBound.fpValue)
      val upperRise = upper < op(argUpperBound.fpValue.nextUp())
      val distance = argUpperBound.fpValue - argLowerBound.fpValue
      val (newLower, newUpper) = when {
        distance > 2.0 * PI || (distance > PI && lowerRise == upperRise)->
          -1.0 to 1.0
        distance < PI && lowerRise && upperRise ->
          lower.nextDown() to upper.nextUp()
        distance < PI && !lowerRise && !upperRise ->
          upper.nextDown() to lower.nextUp()
        lowerRise ->
          kotlin.math.min(lower, upper).nextDown() to 1.0
        else ->
          -1.0 to kotlin.math.max(lower, upper).nextUp()
      }
      filteredMapOf(result to Interval(resInterval, newLower, newUpper), argument to argInterval)
    }
  }
}