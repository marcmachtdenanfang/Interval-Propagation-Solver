package org.mcnip.solver

import org.mcnip.solver.Model.IPSNumber
import org.mcnip.solver.Model.Interval
import org.mcnip.solver.Model.Type

class BiContractions(intervals: Map<String, Interval>, names: Array<String>) {
  val result = names[0]
  val fstArg = names[1]
  val sndArg = names[2]
  val resInterval = intervals.getValue(result)
  val fstInterval = intervals.getValue(fstArg)
  val sndInterval = intervals.getValue(sndArg)
  val resLowerBound: IPSNumber = resInterval.lowerBound
  val resUpperBound: IPSNumber = resInterval.upperBound
  val fstLowerBound: IPSNumber = fstInterval.lowerBound
  val fstUpperBound: IPSNumber = fstInterval.upperBound
  val sndLowerBound: IPSNumber = sndInterval.lowerBound
  val sndUpperBound: IPSNumber = sndInterval.upperBound
  val type: Type = fstLowerBound.type

  companion object {
    @JvmStatic
    fun add(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mutableMapOf(result to Interval(resInterval, fstLowerBound + sndLowerBound, fstUpperBound + sndUpperBound, false),
      fstArg to Interval(fstInterval, resLowerBound - sndUpperBound, resUpperBound - sndLowerBound, true),
      sndArg to Interval(sndInterval, resLowerBound - fstUpperBound, resUpperBound - fstLowerBound, true))
    }

    @JvmStatic
    fun sub(intervals: Map<String, Interval>, names: Array<String>) = BiContractions(intervals, names).run {
      mutableMapOf(result to Interval(resInterval, fstLowerBound - sndUpperBound, fstUpperBound - sndLowerBound, true),
          fstArg to Interval(fstInterval, resLowerBound + sndLowerBound, resUpperBound + sndUpperBound, false),
          sndArg to Interval(sndInterval, fstLowerBound - resUpperBound, fstUpperBound - resLowerBound, true))
    }
  }

}